package com.ejlchina.searcher.implement;

import com.ejlchina.searcher.SearchResult;
import com.ejlchina.searcher.SearchResultConvertInfo;
import com.ejlchina.searcher.SearchResultResolver;
import com.ejlchina.searcher.SearcherException;
import com.ejlchina.searcher.bean.BeanAware;
import com.ejlchina.searcher.implement.convertor.FieldConvertor;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * 默认查询结果解析器
 * 
 * @author Troy.Zhou @ 2017-03-20
 * 
 */
public class MainSearchResultResolver implements SearchResultResolver {

	
	private FieldConvertor fieldConvertor;

	
	public MainSearchResultResolver() {
	}
	
	
	public MainSearchResultResolver(FieldConvertor fieldConvertor) {
		this.fieldConvertor = fieldConvertor;
	}

	
	@Override
	public <T> List<T> resolve(SearchResultConvertInfo<T> convertInfo, ResultSet dataListResult) throws SQLException {
		Class<T> beanClass = convertInfo.getBeanClass();
		Set<Entry<String, String>> fieldDbAliasEntrySet = convertInfo.getFieldDbAliasEntrySet();
		Map<String, Method> fieldGetMethodMap = convertInfo.getFieldGetMethodMap();
		Map<String, Class<?>> fieldTypeMap = convertInfo.getFieldTypeMap();
		List<T> dataList = new ArrayList<>();
		while (dataListResult.next()) {
			T bean = newInstance(beanClass);
			for (Entry<String, String> entry : fieldDbAliasEntrySet) {
				String field = entry.getKey();
				String dbAlias = entry.getValue();
				Object value = dataListResult.getObject(dbAlias);
				Class<?> fieldType = fieldTypeMap.get(field);
				try {
					value = fieldConvertor.convert(value, fieldType);
				} catch (Exception e) {
					throw new SearcherException(
							"可检索Bean【" + beanClass + "】的属性【" + field + "】的类型【" + fieldType + "】与数据库字段类型不兼容！", e);
				}
				setValue(beanClass, fieldGetMethodMap, bean, field, value);
			}
			if (bean instanceof BeanAware) {
				((BeanAware) bean).afterAssembly();
			}
			dataList.add(bean);
		}
		return dataList;
	}

	private <T> void setValue(Class<T> beanClass, Map<String, Method> fieldGetMethodMap, T bean, String field, Object value) {
		Method method = fieldGetMethodMap.get(field);
		try {
			method.invoke(bean, value);
		} catch (Exception e) {
			throw new SearcherException(
					"为【" + beanClass.getName() + "】的【" + field + "】属性赋值时报错，请检查该属性的set方法参数类型是否正确！", e);
		}
	}

	private <T> T newInstance(Class<T> beanClass) {
		try {
			return beanClass.getDeclaredConstructor().newInstance();
		} catch (Exception e) {
			throw new SearcherException("为【" + beanClass.getName() + "】创建对象时报错，请检查该类中是否有无参构造方法！", e);
		}
	}

	public void setFieldConvertor(FieldConvertor fieldConvertor) {
		this.fieldConvertor = fieldConvertor;
	}
	
	
}