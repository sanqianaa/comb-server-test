package com.netease.napm.test.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * @Description
 * @author hzdaiyuan
 * @date 2016-3-30 下午7:58:18
 */

public class Const {

    // dataType for db
    public static final String UPDATE = "update";
    public static final String QUERY = "query";
    // dataType for db ;method just support this method now
    public static final String GET = "get";
    public static final String POST = "post";

    // charset
    public static final String DEFAULT_CHARSET = "utf-8";

    // param data when get request
    public static final String DATA = "data";

    // requestType; just support http and jdbc now
    public static final String HTTP = "http";
    public static final String JDBC = "jdbc";

    // default view
    public static final String DEFAULT_VIEW = "unused_view";

    public static Set<String> METHOD_LIST = new HashSet<String>();
    static {
        METHOD_LIST.add(GET);
        METHOD_LIST.add(POST);
    }
}
