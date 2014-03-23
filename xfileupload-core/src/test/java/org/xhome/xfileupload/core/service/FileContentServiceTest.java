package org.xhome.xfileupload.core.service;

import java.util.List;

import org.junit.Test;
import org.xhome.db.query.QueryBase;
import org.xhome.xfileupload.FileContent;
import org.xhome.xfileupload.core.AbstractTest;
import org.xhome.xfileupload.core.listener.TestFileContentManageListener;

/**
 * @project xfileupload-core
 * @author jhat
 * @email cpf624@126.com
 * @date Sep 10, 201311:59:42 PM
 * @describe
 */
public class FileContentServiceTest extends AbstractTest {

    private FileContentService fileContentService;

    public FileContentServiceTest() {
        fileContentService = context.getBean(FileContentServiceImpl.class);
        oper.setId(101L);

        ((FileContentServiceImpl) fileContentService)
                        .registerFileContentManageListener(new TestFileContentManageListener());
    }

    @Test
    public void testAddFileContent() {
        FileContent fileContent = new FileContent();
        fileContent.setPath("2015/03/13");
        fileContent.setName("Sabcdefg.jpg");
        fileContent.setOriginal("Sjhat.jpg");
        fileContent.setType("jpeg");
        fileContent.setSize(1024L);
        fileContent.setOwner(1L);
        fileContent.setModifier(1L);
        fileContentService.addFileContent(oper, fileContent);
    }

    @Test
    public void testGetFileContent() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        printFileContent(fileContent);

        fileContent = fileContentService.getFileContent(oper, 1L);
        printFileContent(fileContent);
    }

    @Test
    public void testCountFileContents() {
        QueryBase query = new QueryBase();
        query.addParameter("name", "test");
        logger.info("{}", fileContentService.countFileContents(oper, query));
    }

    @Test
    public void testGetFileContents() {
        QueryBase query = new QueryBase();
        query.addParameter("name", "test");
        List<FileContent> fileContents = fileContentService.getFileContents(
                        oper, query);
        printFileContent(fileContents);
    }

    @Test
    public void testIsFileContentUpdateable() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        logger.info("{}", fileContentService.isFileContentUpdateable(oper,
                        fileContent));
    }

    @Test
    public void testIsFileContentDeleteable() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        logger.info("{}", fileContentService.isFileContentDeleteable(oper,
                        fileContent));
    }

    @Test
    public void testIsFileContentLocked() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        logger.info("{}", fileContentService.isFileContentLocked(oper,
                        fileContent));
    }

    @Test
    public void testLockFileContent() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        fileContentService.lockFileContent(oper, fileContent);
    }

    @Test
    public void testUnlockFileContent() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        fileContentService.unlockFileContent(oper, fileContent);
    }

    @Test
    public void testUpdateFileContent() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        fileContent.setId(100L);
        // fileContent.setVersion(11);
        int r = fileContentService.updateFileContent(oper, fileContent);
        logger.info("result:" + r);
    }

    @Test
    public void testDeleteFileContent() {
        FileContent fileContent = fileContentService.getFileContent(oper, 1L);
        fileContentService.deleteFileContent(oper, fileContent);
    }

}
