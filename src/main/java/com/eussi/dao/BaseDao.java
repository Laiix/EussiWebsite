package com.eussi.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.SessionFactoryUtils;
import org.springframework.util.Assert;

/**
 * DAO基类，其它DAO可以直接继承这个DAO，不但可以复用共用的方法，还可以获得泛型的好处。
 */
public class BaseDao<T>{
	private Class<T> entityClass;

	private HibernateTemplate hibernateTemplate;
	/**
	 * 通过反射获取子类确定的泛型类
	 */
	public BaseDao() {
		Type genType = getClass().getGenericSuperclass();
		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
		entityClass = (Class) params[0];
	}



	/**
	 * 根据ID加载PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T load(Serializable id) {
		return (T) getHibernateTemplate().load(entityClass, id); //load方法检索不到的话会抛出org.hibernate.ObjectNotFoundException异常
	}

	/**
	 * 根据ID获取PO实例
	 * 
	 * @param id
	 * @return 返回相应的持久化PO实例
	 */
	public T get(Serializable id) {
		return (T) getHibernateTemplate().get(entityClass, id);//get方法检索不到的话会返回null
	}

	/**
	 * 获取PO的所有对象
	 * 
	 * @return
	 */
	public List<T> loadAll() {
		return getHibernateTemplate().loadAll(entityClass);
	}
	
	/**
	 * 保存PO
	 * 
	 * @param entity
	 */
	public void save(T entity) {
		getHibernateTemplate().save(entity);
	}

	/**
	 * 删除PO
	 * 
	 * @param entity
	 */
	public void remove(T entity) {
		getHibernateTemplate().delete(entity);
	}

	/**
	 * 更改PO
	 * 
	 * @param entity
	 */
	public void update(T entity) {
		getHibernateTemplate().update(entity);
	}

	/**
	 * 执行HQL查询
	 * 
	 * @param hql
	 * @return 查询结果
	 */
	public List find(String hql) {
		return this.getHibernateTemplate().find(hql);
	}

	/**
	 * 执行带参的HQL查询
	 * 
	 * @param hql
	 * @param params
	 * @return 查询结果
	 */
	public List find(String hql, Object... params) {
		return this.getHibernateTemplate().find(hql,params);
	}
    
	/**
	 * 对延迟加载的实体PO执行初始化
	 * @param entity
	 */
	public void initialize(Object entity) {
		this.getHibernateTemplate().initialize(entity);
	}

    /**
     * 当元素或者元素的lazy属性为true时，load() or get() or find()加载这些对象时，
     * Hibernate不会马上产生任何select语句，只是产生一个Obj代理类实例，
     * 只有在session没有关闭的情况下运行Obj.getXxx()时才会执行select语句从数据库加载对象，
     * 如果没有运行任何Obj.getXxx()方法，而session已经关闭，Obj已成游离状态，
     * 此时再运行Obj.getXxx()方法，
     * Hibernate就会抛出"Could not initialize proxy - the owning Session was closeed"的异常，
     * 是说Obj代理类实例无法被初始化。
     * 然而想在Session关闭之前不调用Obj.getXxx()方法而关闭Session之后又要用，
     * 此时只要在Session关闭之前调用Hibernate.initialize(Obj)或者Hibernate.initialize(Obj.getXxx())即可
     */
	
	
	/**
	 * 分页查询函数，使用hql.
	 *
	 * @param pageNo 页号,从1开始.
	 */
	public Page pagedQuery(String hql, int pageNo, int pageSize, Object... values) {
		Assert.hasText(hql);  //spring提供， text 不为null且必须至少包含一个非空格的字符
		Assert.isTrue(pageNo >= 1, "pageNo should start from 1");//对象必须为true
		// Count查询
		String countQueryString = " select count (*) " + removeSelect(removeOrders(hql));
		List countlist = getHibernateTemplate().find(countQueryString, values);
		long totalCount = (Long) countlist.get(0);

		if (totalCount < 1)
			return new Page();
		// 实际查询返回分页对象
        int startIndex = Page.getStartOfPage(pageNo, pageSize);
        Query query = createQuery(hql, values);
        List list = query.setFirstResult(startIndex).setMaxResults(pageSize).list();

		return new Page(startIndex, totalCount, pageSize, list);
	}

	/**
	 * 创建Query对象. 对于需要first,max,fetchsize,cache,cacheRegion等诸多设置的函数,可以在返回Query后自行设置.
	 * 留意可以连续设置,如下：
	 * <pre>
	 * dao.getQuery(hql).setMaxResult(100).setCacheable(true).list();
	 * </pre>
	 * 调用方式如下：
	 * <pre>
	 *        dao.createQuery(hql)
	 *        dao.createQuery(hql,arg0);
	 *        dao.createQuery(hql,arg0,arg1);
	 *        dao.createQuery(hql,new Object[arg0,arg1,arg2])
	 * </pre>
	 *
	 * @param values 可变参数.
	 */
	public Query createQuery(String hql, Object... values) {
		Assert.hasText(hql);
		Query query = getSession().createQuery(hql);
		for (int i = 0; i < values.length; i++) {
			query.setParameter(i, values[i]);
		}
		return query;
	}
	/**
	 * 去除hql的select 子句，未考虑union的情况,用于pagedQuery.
	 *
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeSelect(String hql) {
		Assert.hasText(hql);
		int beginPos = hql.toLowerCase().indexOf("from");
		Assert.isTrue(beginPos != -1, " hql : " + hql + " must has a keyword 'from'");
		return hql.substring(beginPos);
	}
	
	/**
	 * 去除hql的orderby 子句，用于pagedQuery.
	 *
	 * @see #pagedQuery(String,int,int,Object[])
	 */
	private static String removeOrders(String hql) {
		Assert.hasText(hql);
		Pattern p = Pattern.compile("order\\s*by[\\w|\\W|\\s|\\S]*", Pattern.CASE_INSENSITIVE); //\s代表空白字符 后面代表单词，非单词，空白，非空白 后面的模式代表不区分大小写的匹配
		Matcher m = p.matcher(hql);
		StringBuffer sb = new StringBuffer(); //将匹配order by正则的内容替换为空字符串
		while (m.find()) {
			m.appendReplacement(sb, "");
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public HibernateTemplate getHibernateTemplate() {
		return hibernateTemplate;
	}

	@Autowired
	public void setHibernateTemplate(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
    public  Session getSession() {
        return hibernateTemplate.getSessionFactory().getCurrentSession();
    }
	
}
