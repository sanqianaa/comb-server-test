#接口：
##一、path设置接口
/expectation  POST
body={"path":"hello","delay":10000,"statusCode":210}

return： 
{
  "msg": "add path success,config:path=hello;code=210;delay=10000",
  "code": 200
}

##二、查询所有已配置path
/map GET

return:
{
  "pathMap": {
    "hello": {
      "path": "hello",
      "statusCode": 210,
      "delay": 10000
    }
  },
  "code": 200
}

##三、测试配置接口
/{path}?delay=10000&code=200
(如果不传delay或code则用接口配置默认delay/code)

return:
{
  "delay": 10000,
  "code": 210
}
