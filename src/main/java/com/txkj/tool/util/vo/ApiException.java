package com.txkj.tool.util.vo;

import com.alibaba.fastjson2.JSON;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

@Data
@Accessors(chain = true)
public class ApiException extends RuntimeException {

    //  异常中保证的返回信息
    private int code=500;
    private Object data;
    List<StackTraceElement> simpleStackTrace=new ArrayList<>();


    public ApiException(String message, Throwable cause) {
        super(message, cause);
        StackTraceElement[] stackTrace = cause.getStackTrace();
    }

    public ApiException(String message) {
        super(message);
    }

    public String simpleStack(){
        StackTraceElement[] stackTrace = this.getStackTrace();
        for (StackTraceElement item : stackTrace) {
            /*if(stackTraceElement.get){

            }*/
            String claName=item.getClassName();
            if(claName.startsWith("org.springframework.web.servlet.")){
                continue;
            }
            if(claName.startsWith("org.springframework.web.filter.")){
                continue;
            }
            if(claName.startsWith("javax.servlet.http.HttpServlet")){
                continue;
            }
            if(claName.startsWith("org.eclipse.jetty.servlet")){
                continue;
            }
            if(claName.startsWith("org.eclipse.jetty.server")){
                continue;
            }
            if(claName.startsWith("jdk.internal.reflect.NativeMethodAccessorImpl")){
                continue;
            }
            System.out.println(JSON.toJSONString(item));
        }
        return "";
    }



}
