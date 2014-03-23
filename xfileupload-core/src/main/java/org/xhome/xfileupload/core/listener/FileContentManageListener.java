package org.xhome.xfileupload.core.listener;

import org.xhome.xauth.User;
import org.xhome.xfileupload.FileContent;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @date Sep 10, 201311:53:05 PM
 * @description 上传文件管理监听器接口
 */
public interface FileContentManageListener {

    /**
     * 上传文件管理前监听器接口
     * 
     * @param oper
     *            执行该操作的用户
     * @param action
     *            操作类型
     * @param fileContent
     *            待管理的上传文件信息
     * @param args
     *            除fileContent之外的方法调用参数
     * @return 是否允许执行该操作：true/false（允许/不允许）
     */
    public boolean beforeFileContentManage(User oper, short action,
                    FileContent fileContent, Object... args);

    /**
     * 上传文件管理后监听器接口
     * 
     * @param oper
     *            执行该操作的用户
     * @param action
     *            操作类型
     * @param result
     *            操作结果
     * @param fileContent
     *            待管理的上传文件信息
     * @param args
     *            除fileContent之外的方法调用参数
     * @return 是否允许执行该操作：true/false（允许/不允许）
     */
    public void afterFileContentManage(User oper, short action, short result,
                    FileContent fileContent, Object... args);

}
