namespace java com.acupt.acuprpc.protocol.thrift.proto
service ThriftService{
    InvokeResponse invokeMethod(1:InvokeRequest invokeRequest)
}

struct InvokeRequest{
1: required string appName;
2: required string serviceName;
3: required string methodName;
4: required list<string> orderedParameter;
}

struct InvokeResponse{
1: required i32 code;
2: optional string message;
3: optional string result;
}