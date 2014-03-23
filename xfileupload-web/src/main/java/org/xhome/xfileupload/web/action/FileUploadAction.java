package org.xhome.xfileupload.web.action;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.xhome.common.constant.Status;
import org.xhome.crypto.Base64;
import org.xhome.util.FileUtils;
import org.xhome.xauth.User;
import org.xhome.xauth.web.util.AuthUtils;
import org.xhome.xfileupload.FileContent;
import org.xhome.xfileupload.core.service.FileContentService;
import org.xhome.xfileupload.web.util.FileUploadUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @project xfileupload-web
 * @blogor jhat
 * @email cpf624@126.com
 * @date Aug 13, 201310:49:20 PM
 * @description
 */
@Controller
public class FileUploadAction {

    @Autowired
    private FileContentService fileContentService;

    // 保存路径
    public final static String SAVE_PATH              = "upload";

    public final static int    BUFSIZE                = 4096;

    public final static String UE_SEPARATE            = "ue_separate_ue";

    // 文件允许格式
    private String[]           allowFiles             = { ".rar", ".doc",
                    ".docx", ".zip", ".pdf", ".txt", ".swf", ".wmv", ".gif",
                    ".png", ".jpg", ".jpeg", ".bmp"  };

    private Logger             logger                 = LoggerFactory.getLogger(this
                                                                      .getClass());

    public final static String RM_FILE_UPLOAD         = "xfileupload/upload";
    public final static String RM_FILE_UPLOADS        = "xfileupload/uploads";
    public final static String RM_UE_FILE             = "xfileupload/ue_file";
    public final static String RM_UE_IMAGE            = "xfileupload/ue_image";
    public final static String RM_UE_IMAGE_MANAGER    = "xfileupload/ue_image_manager";
    public final static String RM_UE_SCRAWL           = "xfileupload/ue_scrawl";
    public final static String RM_UE_GET_MOVIE        = "xfileupload/ue_get_movie";
    public final static String RM_UE_GET_REMOTE_IMAGE = "xfileupload/ue_get_remote_image";

    /**
     * 文件上传（单个上传）
     * 
     * @param upfile
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = RM_FILE_UPLOAD, method = RequestMethod.POST)
    public void uploadFile(@RequestParam MultipartFile upfile,
                    HttpServletRequest request, HttpServletResponse response) {
        FileContent fileContent = this.saveFile(upfile, request);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonGenerator generator = objectMapper.getFactory()
                            .createGenerator(response.getOutputStream(),
                                            JsonEncoding.UTF8);
            generator.writeObject(fileContent);
            generator.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 文件上传(批量上传）
     * 
     * @param upfiles
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = RM_FILE_UPLOADS, method = RequestMethod.POST)
    public void uploadFiles(@RequestParam MultipartFile[] upfiles,
                    HttpServletRequest request, HttpServletResponse response) {
        FileContent fileContent = null;
        List<FileContent> fileContents = new ArrayList<FileContent>();
        for (MultipartFile upfile : upfiles) {
            fileContent = this.saveFile(upfile, request);
            fileContents.add(fileContent);
        }

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonGenerator generator = objectMapper.getFactory()
                            .createGenerator(response.getOutputStream(),
                                            JsonEncoding.UTF8);
            generator.writeObject(fileContents);
            generator.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * UEditor文件上传
     * 
     * @param upfile
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = RM_UE_FILE, method = RequestMethod.POST)
    public void UEFileUP(@RequestParam MultipartFile upfile,
                    HttpServletRequest request, HttpServletResponse response) {
        FileContent fileContent = this.saveFile(upfile, request);
        short status = fileContent.getStatus();
        String state = "", url = "", fileType = "", original = "";
        if (status == Status.SUCCESS) {
            state = "SUCCESS";
            url = fileContent.getFullURL();
            fileType = fileContent.getType();
            original = fileContent.getOriginal();
        } else if (status == Status.BLOCKED) {
            state = "文件上传被禁止";
        } else {
            state = "文件上传失败";
        }
        try {
            String result = "{'url':'" + url + "','fileType':'" + fileType
                            + "','state':'" + state + "','original':'"
                            + original + "', 'title':'" + original + "'}";
            response.getWriter().print(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * UEditor 图片上传
     * 
     * @param upfile
     * @param request
     * @param response
     */
    @RequestMapping(value = RM_UE_IMAGE, method = RequestMethod.POST)
    public void UEImageUP(@RequestParam MultipartFile upfile,
                    HttpServletRequest request, HttpServletResponse response) {
        this.UEFileUP(upfile, request, response);
    }

    /**
     * UEditor 图片上传保存路径询问
     * 
     * @param fetch
     * @param request
     * @param response
     */
    @RequestMapping(value = RM_UE_IMAGE, method = RequestMethod.GET)
    public void UEImageFetch(@RequestParam String fetch,
                    HttpServletRequest request, HttpServletResponse response) {
        try {
            response.setHeader("Content-Type", "text/javascript");
            response.getWriter()
                            .print("updateSavePath(['" + SAVE_PATH + "']);");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * UEditor 图片在线管理
     * 
     * @param request
     * @param response
     */
    @RequestMapping(value = RM_UE_IMAGE_MANAGER)
    public void UEImageManager(HttpServletRequest request,
                    HttpServletResponse response) {
        String realSavePath = this.getRealSavePath(request);
        List<File> files = this.getImageFiles(realSavePath,
                        new ArrayList<File>());

        String imgStr = "";
        realSavePath += File.separator;
        for (File file : files) {
            imgStr += file.getPath().replace(realSavePath, "") + UE_SEPARATE;
        }
        if (imgStr != "") {
            int pos = imgStr.lastIndexOf(UE_SEPARATE);
            if (pos > 0) {
                imgStr = imgStr.substring(0, pos);
            }
            imgStr = imgStr.replace(File.separator, "/").trim();
        }
        try {
            response.getWriter().print(imgStr);
        } catch (Exception e) {}
    }

    /**
     * UEditor 涂鸦背景上传
     * 
     * @param action
     * @param upfile
     * @param request
     * @param response
     */
    @RequestMapping(value = RM_UE_SCRAWL, method = RequestMethod.POST, params = { "action" })
    public void UEScrawlImage(@RequestParam(required = false) String action,
                    @RequestParam(required = false) MultipartFile upfile,
                    HttpServletRequest request, HttpServletResponse response) {
        // 涂鸦背景上传
        FileContent fileContent = this.saveFile(upfile, request);
        short status = fileContent.getStatus();
        String state = "", url = "";
        if (status == Status.SUCCESS) {
            state = "SUCCESS";
            url = fileContent.getFullURL();
        } else if (status == Status.BLOCKED) {
            state = "文件上传被禁止";
        } else {
            state = "文件上传失败";
        }
        try {
            String result = "<script>parent.ue_callback('" + url + "','"
                            + state + "')</script>";
            response.getWriter().print(result);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * UEditor 涂鸦上传
     * 
     * @param content
     * @param request
     * @param response
     */
    @RequestMapping(value = RM_UE_SCRAWL, method = RequestMethod.POST)
    public void UEScrawlUP(@RequestParam(required = false) String content,
                    HttpServletRequest request, HttpServletResponse response) {
        String url = "", state = "";
        try {
            byte[] data = Base64.decode(content);
            for (int i = 0; i < data.length; ++i) {
                if (data[i] < 0) {
                    data[i] += 256;
                }
            }

            FileContent fileContent = this.saveFile("scrawl.png",
                            new ByteArrayInputStream(data), request);
            short status = fileContent.getStatus();
            if (status == Status.SUCCESS) {
                state = "SUCCESS";
                url = fileContent.getFullURL();
            } else if (status == Status.BLOCKED) {
                state = "文件上传被禁止";
            } else {
                state = "文件上传失败";
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            state = "未知错误";
        }
        try {
            response.getWriter().print(
                            "{'url':'" + url + "',state:'" + state + "'}");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value = RM_UE_GET_REMOTE_IMAGE)
    public void UEGetRemoteImage(HttpServletRequest request,
                    HttpServletResponse response) {
        String srcUrl = "", state = "", resultUrl = "";
        try {
            state = "远程图片抓取成功！";
            srcUrl = request.getParameter("upfile");
            String[] remoteImageUrls = srcUrl.split(UE_SEPARATE);
            String[] imageUrls = new String[remoteImageUrls.length];

            for (int i = 0; i < remoteImageUrls.length; i++) {
                String originalName = remoteImageUrls[i];
                int pos = originalName.indexOf("/");
                if (pos > 0) {
                    originalName = originalName.substring(pos);
                }
                //格式验证
                if (!isImageFile(originalName)) {
                    state = "图片类型不正确！";
                    continue;
                }
                //大小验证
                HttpURLConnection.setFollowRedirects(false);
                HttpURLConnection conn = (HttpURLConnection) new URL(
                                remoteImageUrls[i]).openConnection();
                if (conn.getContentType().indexOf("image") == -1) {
                    state = "请求地址头不正确";
                    continue;
                }
                if (conn.getResponseCode() != 200) {
                    state = "请求地址不存在！";
                    continue;
                }

                FileContent fileContent = this.saveFile(originalName,
                                conn.getInputStream(), request);

                short status = fileContent.getStatus();
                if (status == Status.SUCCESS) {
                    state = "SUCCESS";
                    imageUrls[i] = fileContent.getFullURL();
                } else if (status == Status.BLOCKED) {
                    state = "文件上传被禁止";
                } else {
                    state = "文件上传失败";
                }
            }
            for (int i = 0; i < imageUrls.length; i++) {
                resultUrl += imageUrls[i] + UE_SEPARATE;
            }
            int pos = resultUrl.lastIndexOf(UE_SEPARATE);
            if (pos > 0) {
                resultUrl = resultUrl.substring(0, pos);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            state = "未知错误";
        }
        try {
            response.getWriter().print(
                            "{'url':'" + resultUrl + "','tip':'" + state
                                            + "','srcUrl':'" + srcUrl + "'}");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @RequestMapping(value = RM_UE_GET_MOVIE)
    public void UEGetMovie(HttpServletRequest request,
                    HttpServletResponse response) {
        StringBuffer readOneLineBuff = new StringBuffer();
        String content = "";
        String searchkey = request.getParameter("searchKey");
        String videotype = request.getParameter("videoType");
        try {
            searchkey = URLEncoder.encode(searchkey, "utf-8");
            URL url = new URL(
                            "http://api.tudou.com/v3/gw?method=item.search&appKey=myKey&format=json&kw="
                                            + searchkey
                                            + "&pageNo=1&pageSize=20&channelId="
                                            + videotype
                                            + "&inDays=7&media=v&sort=s");
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                            conn.getInputStream(), "utf-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                readOneLineBuff.append(line);
            }
            content = readOneLineBuff.toString();
            reader.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        try {
            response.getWriter().print(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存文件
     * 
     * @param originalName
     *            文件原始名
     * @param in
     * @param request
     * @return
     */
    private FileContent saveFile(String originalName, InputStream in,
                    HttpServletRequest request) {
        short status = Status.SUCCESS;
        // 文件类型校验
        boolean allow = false;
        String original = originalName.toLowerCase();
        for (String type : allowFiles) {
            if (original.endsWith(type)) {
                allow = true;
                break;
            }
        }

        String saveFolder = "", fileName = "", fileType = "";
        long fileSize = 0;
        File saveFile = null;
        if (allow) {
            // 文件名后缀
            fileType = this.getFileExt(originalName);
            // 生成文件保存路径
            saveFolder = this.generateSaveFolder();
            // 生成文件保存名
            fileName = this.generateSaveName() + fileType;
            if (fileType.charAt(0) == '.') {
                fileType = fileType.substring(1);
            }

            try {
                // 创建文件保存绝对路径
                String savePath = this.createSavePath(request, saveFolder);
                saveFile = new File(savePath, fileName);

                FileOutputStream out = new FileOutputStream(saveFile);
                int len = 0;
                byte[] bytes = new byte[BUFSIZE];
                while (-1 != (len = in.read(bytes))) {
                    out.write(bytes, 0, len);
                    fileSize += len;
                }
                out.flush();
                out.close();
                in.close();

                logger.info("save upload file {} to {}", originalName,
                                saveFile.getAbsolutePath());
            } catch (Exception e) {
                logger.error("fail to upload file " + originalName, e);
                status = Status.ERROR;
            }
        } else {
            // 不允许的文件类型
            status = Status.BLOCKED;
            logger.warn("deny to upload file {}", originalName);
        }

        FileContent fileContent = new FileContent();
        if (status == Status.SUCCESS) {
            fileContent.setPath(saveFolder);
            fileContent.setName(fileName);
            fileContent.setOriginal(originalName);
            fileContent.setType(fileType);
            fileContent.setSize(fileSize);

            User oper = AuthUtils.getCurrentUser();
            AuthUtils.setOwner(fileContent);
            AuthUtils.setModifier(fileContent);
            status = (short) fileContentService.addFileContent(oper,
                            fileContent);
            if (status != Status.SUCCESS) {
                // 删除已上传的文件
                try {
                    FileUtils.deleteFile(saveFile);
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            } else {
                FileUploadUtils.addFileContent(request, fileContent);
            }
        }
        fileContent.setStatus(status);
        return fileContent;
    }

    /**
     * 保存上传的文件
     * 
     * @param upfile
     * @param request
     */
    private FileContent saveFile(MultipartFile upfile,
                    HttpServletRequest request) {
        FileContent fileContent = null;
        try {
            fileContent = this.saveFile(upfile.getOriginalFilename(),
                            upfile.getInputStream(), request);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
        return fileContent;
    }

    /**
     * 生成上传文件存放文件名
     * 
     * @return
     */
    private String generateSaveName() {
        Random random = new Random();
        return "" + random.nextInt(10000) + System.currentTimeMillis();
    }

    /**
     * 生成上传文件存放目录
     * 
     * @return
     */
    private String generateSaveFolder() {
        SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd");
        String path = formater.format(new Date());
        return path;
    }

    /**
     * 创建上传文件的保存目录
     * 
     * @param request
     * @param path
     * @return
     */
    private String createSavePath(HttpServletRequest request, String path) {
        String realSavePath = this.getRealSavePath(request);
        File dir = new File(realSavePath, path);
        if (!dir.exists()) {
            try {
                dir.mkdirs();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                return null;
            }
        }
        return dir.getAbsolutePath();
    }

    /**
     * 获取上传文件保存完成路径
     * 
     * @param request
     * @return
     */
    private String getRealSavePath(HttpServletRequest request) {
        return request.getSession().getServletContext().getRealPath("/")
                        + File.separator + SAVE_PATH;
    }

    /**
     * 获取文件扩展名
     * 
     * @return string
     */
    private String getFileExt(String fileName) {
        int pos = fileName.lastIndexOf(".");
        return pos >= 0 ? fileName.substring(pos) : "";
    }

    /**
     * 获取上传的图片文件
     * 
     * @param realpath
     * @param files
     * @return
     */
    private List<File> getImageFiles(String realpath, List<File> files) {
        File realFile = new File(realpath);
        if (realFile.isDirectory()) {
            File[] subfiles = realFile.listFiles();
            for (File file : subfiles) {
                if (file.isDirectory()) {
                    this.getImageFiles(file.getAbsolutePath(), files);
                } else {
                    if (this.isImageFile(file.getName())) {
                        files.add(file);
                    }
                }
            }
        }
        return files;
    }

    /**
     * 判断是否为图片文件
     * 
     * @param fileName
     * @return
     */
    private boolean isImageFile(String fileName) {
        String[] fileType = { ".gif", ".png", ".jpg", ".jpeg", ".bmp" };
        fileName = fileName.toLowerCase();
        for (String type : fileType) {
            if (fileName.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    public void setFileContentService(FileContentService fileContentService) {
        this.fileContentService = fileContentService;
    }

    public FileContentService getFileContentService() {
        return fileContentService;
    }

    /**
     * @return the allowFiles
     */
    public String[] getAllowFiles() {
        return allowFiles;
    }

    /**
     * @param allowFiles
     *            the allowFiles to set
     */
    public void setAllowFiles(String[] allowFiles) {
        this.allowFiles = allowFiles;
    }

}
