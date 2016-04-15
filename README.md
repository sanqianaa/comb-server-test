#接口：
##一、path设置接口
/expectation  POST
body:
{
    "path":接口,
    "statusCode":返回状态码,
    "delay":响应时间,
    "method":http方法
}
eg:
body:
{
    "path":"test",
    "statusCode":200,
    "delay":100,
    "method":"post"
}
return： 
{
  "msg": "add path success,config:path=test;method=post;code=200;delay=100",
  "code": 200
}

##二、查询所有已配置path
/map GET
return:
{
  "pathMap": {
    "posttest": {
      "path": "test",
      "statusCode": 200,
      "delay": 100,
      "method": "post"
    }
  },
  "code": 200
}

##三、测试配置接口
/{path}?delay=10000&code=200
(如果不传delay或code则用接口配置默认delay/code)

GET接口：
&data={
"requestType":对外请求类型（jdbc或http）,
"dataType":数据类型(jdbc:query,update;http:get,post),
"url":对外请求url,
"body":对外请求数据（http:请求body或者data参数;jdbc:sql语句）
}
eg:
GET test?delay=100&code=200&data={
"requestType":"jdbc",
"dataType":"query",
"url":"jdbc:mysql://localhost:3306/test_ddb?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true",
"body":"show tables;"
}

GET test?delay=100&code=200&data={
"requestType":"http",
"dataType":"post",
"url":"localhost:8080/test?delay=100&code=200",
"body":""
}

POST接口：
参数类型解释同上

eg:
POST test?delay=100&code=200
body:{
"requestType":"jdbc",
"dataType":"query",
"url":"jdbc:mysql://localhost:3306/test_ddb?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true",
"body":"show tables;"
}

return:
{
  "delay": 10000,
  "code": 210
}
##
