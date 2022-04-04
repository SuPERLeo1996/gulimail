package com.leo.mall.search.service;

import com.leo.mall.search.vo.SearchParam;
import com.leo.mall.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam param);
}
