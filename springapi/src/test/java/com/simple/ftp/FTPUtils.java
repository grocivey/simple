package com.simple.ftp;

import com.simple.api.util.MD5Util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class FTPUtils {
    //ftp服务器地址
    private final String hostname;
    //ftp服务器端口号默认为21
    private final Integer port;
    //ftp登录账号
    private final String username;
    //ftp登录密码
    private final String password;

    private final FTPClientWrapper wrapper = new FTPClientWrapper();

    @Slf4j
    static class FTPClientWrapper implements AutoCloseable {

        FTPClient ftpClient = null;

        void init(String hostname, Integer port, String username, String password) throws IOException {
            ftpClient = new FTPClient(); //NOSONAR
            ftpClient.setControlEncoding(StandardCharsets.UTF_8.name());

            log.debug("connecting...ftp服务器: {} ,端口：{}", hostname, port);
            ftpClient.connect(hostname, port); //连接ftp服务器
            ftpClient.login(username, password); //登录ftp服务器
            //被动模式
            ftpClient.enterLocalPassiveMode();
            //设置文件类型二进制
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            int replyCode = ftpClient.getReplyCode(); //是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                log.error("connect failed...ftp服务器 {},端口 {}", hostname, port);
            } else {
                log.debug("connect successful...ftp服务器{}，端口 {}", hostname, port);
            }
        }

        @Override
        public void close() throws Exception {
            if (ftpClient != null && ftpClient.isConnected()) {
                try {
                    ftpClient.disconnect();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public FTPUtils(String hostname, Integer port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    /**
     * 上传文件
     *
     * @param uploadPathname ftp服务保存地址
     * @param uploadFileName 上传到ftp的文件名
     * @param originfilename 待上传文件的名称（绝对地址） *
     * @return
     */
    public boolean uploadFile(String uploadPathname, String uploadFileName, String originfilename) throws IOException {
        InputStream inputStream = Files.newInputStream(new File(originfilename).toPath());
        return uploadFile(uploadPathname, uploadFileName, inputStream);
    }

    /**
     * 上传文件
     *
     * @param uploadPathname ftp服务保存地址
     * @param fileName       上传到ftp的文件名
     * @param inputStream    输入文件流
     * @return Success or not
     */
    public boolean uploadFile(String uploadPathname, String fileName, InputStream inputStream) {
        log.debug("uploadPathname is {}", uploadPathname);
        log.debug("fileName is {}", fileName);
        try (FTPClientWrapper w = this.wrapper) {
            log.debug("开始上传文件");
            w.init(this.hostname, this.port, this.username, this.password);
            w.ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            if (!createDirectory(uploadPathname)) {
                log.error("createDirectory failed");
                return false;
            }
            if (!w.ftpClient.storeFile(fileName, inputStream)) {
                log.error("storeFile failed");
                return false;
            }
            log.debug("上传文件成功");
            return true;
        } catch (Exception e) {
            log.error("上传文件失败", e);
            return false;
        }
    }

    //改变目录路径
    private boolean changeWorkingDirectory(String directory) {
        boolean flag;
        if (wrapper.ftpClient == null) {
            return false;
        }
        try {
            flag = wrapper.ftpClient.changeWorkingDirectory(directory);
            if (flag) {
                log.debug("进入文件夹 {} 成功！", directory);

            } else {
                log.error("进入文件夹 {} 失败！开始创建文件夹", directory);
            }
        } catch (IOException ioe) {
            log.error("", ioe);
            flag = false;
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    private boolean createDirectory(String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(directory)) {
            int start = getStart(directory);
            int end = directory.indexOf("/", start);
            StringBuilder path = new StringBuilder();
            do {
                String subDirectory = remote.substring(start, end);
                path.append("/");
                path.append(subDirectory);
                if (!pathNoExistFile(path.toString(), subDirectory)) {
                    success = false;
                    break;
                }
                start = end + 1;
                end = directory.indexOf('/', start);
                // 检查所有目录是否创建完毕
            } while (end > start);
        }
        return success;
    }

    private boolean pathNoExistFile(String path, String subDirectory) throws IOException {
        boolean result = false;
        if (!existFile(path)) {
            if (makeDirectory(subDirectory)) {
                changeWorkingDirectory(subDirectory);
                result = true;
            } else {
                log.error("创建目录[ {} ]失败", subDirectory);
                changeWorkingDirectory(subDirectory);
            }
        } else {
            changeWorkingDirectory(subDirectory);
        }
        return result;
    }

    private int getStart(String directory) {
        int start;
        if (directory.startsWith("/")) {
            start = 1;
        } else {
            start = 0;
        }
        return start;
    }

    //判断ftp服务器文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        if (wrapper.ftpClient == null) {
            return false;
        }
        FTPFile[] ftpFileArr = wrapper.ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建目录
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        if (wrapper.ftpClient == null) {
            return false;
        }
        try {
            flag = wrapper.ftpClient.makeDirectory(dir);
            if (flag) {
                log.debug("创建文件夹 {},成功！", dir);

            } else {
                log.error("创建文件夹{} 失败", dir);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return flag;
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename, String localpath) {
        boolean flag = false;
        try (FTPClientWrapper w = this.wrapper) {
            log.debug("开始下载文件");
            w.init(this.hostname, this.port, this.username, this.password);
            //切换FTP目录
            w.ftpClient.changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = w.ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                log.debug("文件名: {}", file.getName());
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + "/" + file.getName());
                    log.debug("localAbsoluteFilePath:{}/{}", localpath, file.getName());
                    try (OutputStream os = Files.newOutputStream(localFile.toPath())) {
                        flag = w.ftpClient.retrieveFile(file.getName(), os);
                        errorLog(filename, flag, "downloadFile retrieveFile false,filename is {}");
                    } catch (IOException ex) {
                        log.error("写本地文件失败: ", ex);
                    }
                }
            }
            log.debug("下载文件成功");
        } catch (Exception e) {
            log.error("下载文件失败", e);
        }
        return flag;
    }

    /**
     * 删除文件且备份
     *
     * @param pathname   FTP服务器保存目录
     * @param filename   要删除的文件名称
     * @param backupPath 备份目录
     * @return Success or not
     */
    public boolean deleteFile(String pathname, String filename, String backupPath) {
        try (FTPClientWrapper w = this.wrapper) {
            log.debug("开始删除文件");
            w.init(this.hostname, this.port, this.username, this.password);
            if (StringUtils.isNotBlank(backupPath)) {
                // 复制
                boolean checkFileExist = checkFileExist(pathname, filename);
                if (!checkFileExist) {
                    log.error("文件： {} 不存在", filename);
                    return true;
                }
                boolean copyRes = copyFile(w.ftpClient, filename, pathname, backupPath);
                if (!copyRes) {
                    log.error("复制失败:" + filename);
                    return false;
                }
            }
            //切换FTP目录
            w.ftpClient.changeWorkingDirectory(pathname);
            w.ftpClient.dele(filename);
            log.debug("删除文件成功");
            return true;
        } catch (Exception e) {
            log.error("删除文件失败: ", e);
            return false;
        }
    }

    /**
     * 删除文件且不备份
     *
     * @param pathname FTP服务器保存目录
     * @param filename 要删除的文件名称
     * @return Success or not
     */
    public boolean deleteFile(String pathname, String filename) {
        return deleteFile(pathname, filename, null);
    }

    private boolean checkFileExist(String pathname, String filename) throws IOException {
        boolean exist = false;
        if (wrapper.ftpClient == null) {
            return false;
        }
        //切换FTP目录
        wrapper.ftpClient.changeWorkingDirectory(pathname);
        FTPFile[] ftpFiles = wrapper.ftpClient.listFiles();
        for (FTPFile file : ftpFiles) {
            log.debug("fileName:" + file.getName());
            if (filename.equalsIgnoreCase(file.getName())) {
                exist = true;
                break;
            }
        }
        return exist;
    }

    /**
     * 下载文件，文件流形式给前端
     *
     * @param pathname ftp目录地址
     * @param filename 文件名
     * @param response response
     * @return boolean
     */
    public boolean downloadResponse(String pathname, String filename, String realName, HttpServletResponse response) {
        try (FTPClientWrapper w = this.wrapper) {
            log.debug("准备下载文件");
            w.init(this.hostname, this.port, this.username, this.password);
            //切换FTP目录
            if (!w.ftpClient.changeWorkingDirectory(pathname)) {
                return false;
            }
            boolean checkFileExist = false;
            FTPFile[] ftpFiles = w.ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    //找到文件
                    log.debug("找到文件{}", file.getName());
                    checkFileExist = true;
                    String[] arr;
                    arr = filename.split("\\.");
                    String suffix = arr[arr.length - 1];
                    File tempFile = File.createTempFile("tmp" + System.currentTimeMillis(), "." + suffix); //NOSONAR
                    try (OutputStream os = Files.newOutputStream(tempFile.toPath());
                         BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
                         FileInputStream fileInputStream = new FileInputStream(tempFile)) {
                        log.debug("下载临时文件【{}】中", file.getName());
                        if (w.ftpClient.retrieveFile(file.getName(), os)) {
                            log.debug("下载临时文件成功");
                        } else {
                            log.error("下载临时文件失败");
                            return false;
                        }

                        //处理response
                        // 清空response
                        response.reset();
                        response.setStatus(HttpServletResponse.SC_OK);
                        // 设置response的Header
                        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                        //Content-Disposition的作用：告知浏览器以何种方式显示响应返回的文件，用浏览器打开还是以附件的形式下载到本地保存
                        //attachment表示以附件方式下载 inline表示在线打开 "Content-Disposition: inline; filename=文件名.mp3"
                        // filename表示文件的默认名称，因为网络传输只支持URL编码的相关支付，因此需要将文件名URL编码后进行传输,前端收到后需要反编码才能获取到真正的名称
                        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + URLEncoder.encode(realName, StandardCharsets.UTF_8.name()));
                        // 告知浏览器文件的大小
                        response.addHeader(HttpHeaders.CONTENT_LENGTH, "" + tempFile.length());
                        log.debug("tempFile.length()=" + tempFile.length());
                        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
                        //输出流
                        FileCopyUtils.copy(fileInputStream, out);
                        out.flush();
                        break;
                    }catch (Exception ex) {
                        log.error("传输文件发生异常: ", ex);
                        return false;
                    }
                }
            }
            errorLog(filename, !checkFileExist, "文件：{} 不存在");
        }catch (Exception e) {
            log.error("下载文件失败：{}", e.getMessage());
            return false;
        }

        return true;
    }


    public File downloadTempFile(String pathname, String filename, String realName) {
        try (FTPClientWrapper w = this.wrapper) {
            log.debug("准备下载文件");
            w.init(this.hostname, this.port, this.username, this.password);
            //切换FTP目录
            if (!w.ftpClient.changeWorkingDirectory(pathname)) {
                return null;
            }
            boolean checkFileExist = false;
            FTPFile[] ftpFiles = w.ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    //找到文件
                    log.debug("找到文件{}", file.getName());
                    checkFileExist = true;
                    String[] arr;
                    arr = filename.split("\\.");
                    String suffix = arr[arr.length - 1];
                    File tempFile = File.createTempFile("tmp" + System.currentTimeMillis(), "." + suffix); //NOSONAR
                    try (OutputStream os = Files.newOutputStream(tempFile.toPath())) {
                        log.debug("下载临时文件【{}】中", file.getName());
                        if (w.ftpClient.retrieveFile(file.getName(), os)) {
                            os.flush();
                            log.debug("下载临时文件成功");
                            return tempFile;
                        } else {
                            log.error("下载临时文件失败");
                            return null;
                        }
                    }catch (Exception ex) {
                        log.error("传输文件发生异常: ", ex);
                        return null;
                    }
                }
            }
            errorLog(filename, !checkFileExist, "文件：{} 不存在");
        }catch (Exception e) {
            log.error("下载文件失败：{}", e.getMessage());
            return null;
        }

        return null;
    }


    public boolean copyFile(FTPClient ftpClient, String sourceFileName, String sourceDir, String targetDir) throws IOException {
        ByteArrayInputStream in = null;
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        boolean resultFlag = false;
        if (ftpClient == null) {
            return false;
        }

        if (!ftpClient.changeWorkingDirectory(targetDir)) {
            Boolean flag = createMultiDir(ftpClient, targetDir);// 创建多层目录
            if (!flag) {
                log.error("创建多层目录失败");
                return false;
            }
            ftpClient.changeWorkingDirectory(targetDir);
        }
        log.debug("变更工作路径");
        ftpClient.changeWorkingDirectory(sourceDir);// 变更工作路径   
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);// 设置以二进制流的方式传输
        resultFlag = ftpClient.retrieveFile(sourceFileName, fos);
        errorLog(sourceFileName, resultFlag, "copyFile retrieveFile false,sourceFileName is {}");
        ftpClient.setBufferSize(1024);

        in = new ByteArrayInputStream(fos.toByteArray());

        resultFlag = ftpClient.storeFile(targetDir + File.separator + sourceFileName, in);
        errorLog(sourceFileName, resultFlag, "copyFile storeFile false,sourceFileName is {}");

        return true;
    }

    private void errorLog(String sourceFileName, boolean resultFlag, String s) {
        if (resultFlag) {
            log.error(s, sourceFileName);
//            throw new MyCustomException().setMsg("文件："+sourceFileName+" 不存在");
        }
    }

    public static boolean createMultiDir(FTPClient ftpClient, String multiDir) throws IOException {
        boolean bool = false;
        if (StringUtils.isBlank(multiDir)) {
            return false;
        }
        String[] dirs = multiDir.split("/");
        ftpClient.changeWorkingDirectory("/");
        for (int i = 1; i < dirs.length; i++) {
            if (!ftpClient.changeWorkingDirectory(dirs[i]) && ftpClient.makeDirectory(dirs[i])) {
                return false;
            }
            bool = true;
        }
        return bool;
    }

    @SneakyThrows
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String md5Code = "f47400fcabfb6cfc87098855593e837d";
//        String md5Code = "bdc02c69e41494c660985bc51a44d25d";//bdc02c69e41494c660985bc51a44d25d  导入模板.xlsx
        //f47400fcabfb6cfc87098855593e837d  23.1.HQ.FSU.TT.AA00.R_DT10-INCe.bin
        ExecutorService service = Executors.newFixedThreadPool(20);
        AtomicInteger success = new AtomicInteger(0);
        AtomicInteger fail = new AtomicInteger(0);

        for (int i = 0; i < 50; i++) {
            int finalI = i;
            service.execute(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    String filename = "23.1.HQ.FSU.TT.AA00.R_DT10-INCe.bin";
//                    String filename = "导入模板.xlsx";
                    FTPUtils ftpUtils = new FTPUtils("ip", 21, "username", "password");
//                    File file = ftpUtils.downloadTempFile("/fsu/upgrade/execelTemplate", filename, filename);
                    File file = ftpUtils.downloadTempFile("/update/yytd", filename, filename);
                    if (file != null){
                        String tMd5 = MD5Util.getMD5(file);
                        System.out.println(tMd5+" ----- "+ finalI);
                        if (tMd5.equals(md5Code)){
                            success.addAndGet(1);
                        }else {
                            fail.addAndGet(1);
                        }
                        file.delete();
                    }
                    ftpUtils = null;
                }
            });

        }
        service.shutdown();
        do {
            TimeUnit.SECONDS.sleep(1);
        }while (!service.isTerminated());
        System.out.println("一致："+success);
        System.out.println("不一致："+fail);

    }
}
