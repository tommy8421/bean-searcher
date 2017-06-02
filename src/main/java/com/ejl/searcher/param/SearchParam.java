package com.ejl.searcher.param;

import java.util.ArrayList;
import java.util.List;

import com.ejl.utils.StrUtils;

/**
 * @author Troy.Zhou @ 2017-03-21
 *
 *         检索参数
 * 
 */
public class SearchParam {

	/**
	 * 排序字段（用于排序）
	 */
	private String sort;

	/**
	 * 排序方法：desc, asc（用于排序）
	 */
	private String order;

	/**
	 * 查询最大条数（用于分页）
	 */
	private Integer max = 10;

	/**
	 * 查询偏移条数（用于分页）
	 */
	private Long offset;

	/**
	 * 过滤检索参数列表
	 */
	private List<FilterParam> filterParamList = new ArrayList<>();

	public void removeUselessFilterParam() {
		int size = filterParamList.size();
		for (int i = size - 1; i >= 0; i--) {
			FilterParam param = filterParamList.get(i);
			FilterOperator op = param.getOperator();
			String value = param.getValue();
			String value2 = param.getValue2();
			if (StrUtils.isBlank(value) && StrUtils.isBlank(value2) && op != FilterOperator.Empty
					&& op != FilterOperator.NotEmpty) {
				filterParamList.remove(i);
			}
		}
	}

	public void addFilterParam(FilterParam filterParam) {
		filterParamList.add(filterParam);
	}

	public List<FilterParam> getFilterParamList() {
		return filterParamList;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}

	@Override
	public String toString() {
		String str = "SearchParam: \n\tsort = " + sort + "\n\torder=" + order + "\n\tmax=" + max + "\n\toffset="
				+ offset + "\n\tfilterParamList:";
		for (FilterParam param : filterParamList) {
			str += "\n\t\t" + param.toString();
		}
		return str;
	}

}
