package org.xhome.xfileupload.core;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.xhome.xauth.User;
import org.xhome.xfileupload.FileContent;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @date Sep 2, 20139:54:46 PM
 * @describe
 */
public abstract class AbstractTest {

    protected ApplicationContext context = null;
    protected Logger             logger  = LoggerFactory.getLogger(this
                                                         .getClass());
    protected User               oper;

    public AbstractTest() {
        context = new ClassPathXmlApplicationContext(
                        "classpath:applicationContext.xml");
        oper = new User();
        oper.setId(101L);
    }

    protected void printFileContent(List<FileContent> fileContents) {
        if (fileContents != null) {
            for (FileContent fileContent : fileContents) {
                printFileContent(fileContent);
            }
        }
    }

    protected void printFileContent(FileContent fileContent) {
        System.out.println("Path: " + fileContent.getPath() + "\tName: "
                        + fileContent.getName() + "\tOriginal: "
                        + fileContent.getOriginal() + "\tType: "
                        + fileContent.getType() + "\tSize: "
                        + fileContent.getSize());
    }

}
