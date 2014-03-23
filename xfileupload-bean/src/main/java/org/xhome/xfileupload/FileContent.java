package org.xhome.xfileupload;

import org.xhome.common.Base;

/**
 * @project xfileupload-bean
 * @author jhat
 * @email cpf624@126.com
 * @homepage http://pfchen.org
 * @date Mar 13, 2014
 * @describe 上传文件记录
 */
public class FileContent extends Base {

    private static final long serialVersionUID = -679393818562821316L;

    private String            path             = "";                  // 文件保存路径
    private String            name             = "";                  // 上传文件名
    private String            original         = "";                  // 原始文件名
    private String            type             = "";                  // 文件类型
    private long              size             = 0;                   // 文件大小

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path
     *            the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the original
     */
    public String getOriginal() {
        return original;
    }

    /**
     * @param original
     *            the original to set
     */
    public void setOriginal(String original) {
        this.original = original;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * @param size
     *            the size to set
     */
    public void setSize(long size) {
        this.size = size;
    }

    /**
     * 获取完整URL（包含参数）
     * 
     * @return
     */
    public String getFullURL() {
        return this.path + "/" + this.getName() + "?id=" + this.getId();
    }

    /**
     * 获取不带任何参数的URL
     * 
     * @return
     */
    public String getURL() {
        return this.path + "/" + this.getName();
    }

}
