# xfileupload
文件上传管理组件，提供文件上传和管理功能，所有上传的文件将保存在**webapp/upload**目录中。

## 使用文件上传功能需要对Spring做如下配置

        <!-- SpringMVC上传文件时，需要配置MultipartResolver处理器 -->  
        <bean id="multipartResolver" class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
            <property name="defaultEncoding" value="UTF-8"/>
            <!-- 指定所上传文件的总大小不能超过200KB。注意maxUploadSize属性的限制不是针对单个文件，而是所有文件的容量之和 -->
            <property name="maxUploadSize" value="200000000"/>
        </bean>

## 文件上传链接
### 单个文件上传
    访问路径： xfileupload/upload
    文件参数： upfile
### 批量文件上传
    访问路径： xfileupload/uploads
    文件参数： upfiles
### 完整支持UEditor涉及文件上传的功能

## 上传文件访问路径
    /upload/yyyy.suffix?id=zzzz
