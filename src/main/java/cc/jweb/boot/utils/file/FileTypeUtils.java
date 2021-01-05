package cc.jweb.boot.utils.file;

import cc.jweb.boot.utils.lang.StringUtils;

import java.util.*;

public class FileTypeUtils {

    /**
     * 后缀名与对应文件类型集合
     */
    public final static Map<String, FileType> fileExtTypeMapping = new HashMap<String, FileType>();
    public static final String FILE_TYPE_TEXT = "1";// 文本文档
    public static final String FILE_TYPE_IMAGE = "2";// 图片文件
    public static final String FILE_TYPE_APPLICATION = "3";// 应用文件
    public static final String FILE_TYPE_VIDEO = "4";// 视频文件
    public static final String FILE_TYPE_AUDIO = "5";// 音频文件
    public static final String FILE_TYPE_X_WORLD = "6";// x-world文件
    public static final String FILE_TYPE_DEFAULT = "0";// 其它文件
    // TODO 文件图标待完善
    private static final String[] FILE_TYPE_IMGS = {"img/p/t/file.png", "img/p/t/file.png", "img/p/e/image.gif", "img/b/rtf.gif", "img/p/wmp4ul.gif", "img/p/wmp4ul.gif", "img/b/exam.gif"};
    private static final String[] EXT = {"jpg", "gif", "zip", "rar", "bmp", "png", "doc", "docx", "wps", "xls", "xlsx", "et", "ppt", "pptx", "dps", "html", "xml", "js", "mov", "mp4", "flv", "rm", "rmvb", "wmv", "swf", "txt", "mp3"};
    private static final String[] EXT_NAME = {"JPEG 图像", "GIF 图像", "WinRAR 压缩文件", "rar", "bmp", "png", "DOC文档", "DOCX文档", "WPS文字文档", "XLS文档", "XLSX文档", "WPS表格/工作簿", "PPT文档", "PPTX文档", "WPS演示文档", "HTML 文档", "XML 文档", "JScript Script FileObject", "mov", "mp4", "FLV 文件", "RM 文件", "RMVB 文件", "WMV 文件", "swf", "文本文档", "MP3 音频文件"};
    private static final Object SYNC_LOCK_OBJECT = new Object();
    private static HashMap<String, String> categoryNameMap;

    static {// 图片扩展名
        fileExtTypeMapping.put("art", FileType.IMAGE);
        fileExtTypeMapping.put("bmp", FileType.IMAGE);
        fileExtTypeMapping.put("dib", FileType.IMAGE);
        fileExtTypeMapping.put("gif", FileType.IMAGE);
        fileExtTypeMapping.put("ico", FileType.IMAGE);
        fileExtTypeMapping.put("ief", FileType.IMAGE);
        fileExtTypeMapping.put("jpe", FileType.IMAGE);
        fileExtTypeMapping.put("jpeg", FileType.IMAGE);
        fileExtTypeMapping.put("jpg", FileType.IMAGE);
        fileExtTypeMapping.put("mac", FileType.IMAGE);
        fileExtTypeMapping.put("pbm", FileType.IMAGE);
        fileExtTypeMapping.put("pct", FileType.IMAGE);
        fileExtTypeMapping.put("pgm", FileType.IMAGE);
        fileExtTypeMapping.put("pic", FileType.IMAGE);
        fileExtTypeMapping.put("pict", FileType.IMAGE);
        fileExtTypeMapping.put("png", FileType.IMAGE);
        fileExtTypeMapping.put("pnm", FileType.IMAGE);
        fileExtTypeMapping.put("pnt", FileType.IMAGE);
        fileExtTypeMapping.put("ppm", FileType.IMAGE);
        fileExtTypeMapping.put("psd", FileType.IMAGE);
        fileExtTypeMapping.put("qti", FileType.IMAGE);
        fileExtTypeMapping.put("qtif", FileType.IMAGE);
        fileExtTypeMapping.put("ras", FileType.IMAGE);
        fileExtTypeMapping.put("rgb", FileType.IMAGE);
        fileExtTypeMapping.put("svg", FileType.IMAGE);
        fileExtTypeMapping.put("svgz", FileType.IMAGE);
        fileExtTypeMapping.put("tif", FileType.IMAGE);
        fileExtTypeMapping.put("tiff", FileType.IMAGE);
        fileExtTypeMapping.put("wbmp", FileType.IMAGE);
        fileExtTypeMapping.put("xbm", FileType.IMAGE);
        fileExtTypeMapping.put("xpm", FileType.IMAGE);
        fileExtTypeMapping.put("xwd", FileType.IMAGE);

        // 视频
        fileExtTypeMapping.put("asf", FileType.VIDEO);
        fileExtTypeMapping.put("asx", FileType.VIDEO);
        fileExtTypeMapping.put("avi", FileType.VIDEO);
        fileExtTypeMapping.put("avx", FileType.VIDEO);
        fileExtTypeMapping.put("dv", FileType.VIDEO);
        fileExtTypeMapping.put("flv", FileType.VIDEO);
        fileExtTypeMapping.put("mov", FileType.VIDEO);
        fileExtTypeMapping.put("movie", FileType.VIDEO);
        fileExtTypeMapping.put("mp4", FileType.VIDEO);
        fileExtTypeMapping.put("mpe", FileType.VIDEO);
        fileExtTypeMapping.put("mpeg", FileType.VIDEO);
        fileExtTypeMapping.put("mpega", FileType.VIDEO);
        fileExtTypeMapping.put("mpg", FileType.VIDEO);
        fileExtTypeMapping.put("mpv2", FileType.VIDEO);
        fileExtTypeMapping.put("qt", FileType.VIDEO);
        fileExtTypeMapping.put("wmv", FileType.VIDEO);
        fileExtTypeMapping.put("rm", FileType.VIDEO);
        fileExtTypeMapping.put("rmvb", FileType.VIDEO);

        // 文档
        fileExtTypeMapping.put("txt", FileType.DOCUMENT);
        fileExtTypeMapping.put("doc", FileType.DOCUMENT);
        fileExtTypeMapping.put("docx", FileType.DOCUMENT);
        fileExtTypeMapping.put("xls", FileType.DOCUMENT);
        fileExtTypeMapping.put("xlsx", FileType.DOCUMENT);
        fileExtTypeMapping.put("pdf", FileType.DOCUMENT);
        fileExtTypeMapping.put("ppt", FileType.DOCUMENT);
        fileExtTypeMapping.put("pptx", FileType.DOCUMENT);
        fileExtTypeMapping.put("wps", FileType.DOCUMENT);
        fileExtTypeMapping.put("dps", FileType.DOCUMENT);
        fileExtTypeMapping.put("et", FileType.DOCUMENT);

        //音频
        fileExtTypeMapping.put("ogg", FileType.AUDIO);
        fileExtTypeMapping.put("wav", FileType.AUDIO);
        fileExtTypeMapping.put("flac", FileType.AUDIO);
        fileExtTypeMapping.put("ape", FileType.AUDIO);
        fileExtTypeMapping.put("mp3", FileType.AUDIO);
        fileExtTypeMapping.put("wma", FileType.AUDIO);

        fileExtTypeMapping.put("other", FileType.OTHER);

    }

    public static String getExtension(String fileName) {
        if (fileName != null && !fileName.equals("")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase(); // 将后缀统一为小写，方便判断文件类型
        }
        return fileName;
    }

    /**
     * 获取文件类型名称
     *
     * @param fileType
     * @return
     */
    public static String getFileTypeName(Locale loc, String fileType) {
        String[] FILE_TYPE_NAMES = {"其它文件", "文本文档", "图片文件", "应用文件", "视频文件", "音频文件", "x-world文件"};
        if (StringUtils.isEmpty(fileType)) {
            return FILE_TYPE_NAMES[0];
        }
        int i = Integer.parseInt(fileType);
        return FILE_TYPE_NAMES[i];
    }

    /**
     * 获取文件类型
     *
     * @param fileType
     * @return
     */
    public static String getFileTypeName(String fileType) {
        String fileTypeName = "";
        for (int i = 0; i < EXT.length; i++) {
            if (EXT[i].equalsIgnoreCase(fileType)) {
                fileTypeName = EXT_NAME[i];
                break;
            }
        }
        if (StringUtils.isEmpty(fileTypeName)) {
            fileTypeName = "未知类型";
        }
        return fileTypeName;
    }

    /**
     * 获取文件后缀图标
     *
     * @param suffix 文件后缀名/扩展名
     * @return
     */
    public static String getFileSuffixImg(String suffix) {
        String suffixImg = "";
        for (int i = 0; i < EXT.length; i++) {
            if (EXT[i].equalsIgnoreCase(suffix)) {
                suffixImg = "img/b/res/fileType/" + EXT[i] + ".gif";
                break;
            }
        }
        if (StringUtils.isEmpty(suffixImg)) {
            suffixImg = "img/b/res/fileType/unknown.gif";
        }
        return suffixImg;
    }

    /**
     * 获取文件对应图标
     *
     * @param fileType
     * @return
     */
    public static String getFileTypeImg(String fileType) {
        if (StringUtils.isEmpty(fileType)) {
            return FILE_TYPE_IMGS[0];
        }
        int i = Integer.parseInt(fileType);
        return FILE_TYPE_IMGS[i];
    }

    /**
     * 获取文件类型集合
     *
     * @param loc
     * @return
     */
    public static List<String[]> getFileTypeList(Locale loc) {
        ArrayList<String[]> list = new ArrayList<String[]>();
        list.add(new String[]{FILE_TYPE_TEXT, getFileTypeName(loc, FILE_TYPE_TEXT)});
        list.add(new String[]{FILE_TYPE_IMAGE, getFileTypeName(loc, FILE_TYPE_IMAGE)});
        list.add(new String[]{FILE_TYPE_APPLICATION, getFileTypeName(loc, FILE_TYPE_APPLICATION)});
        list.add(new String[]{FILE_TYPE_VIDEO, getFileTypeName(loc, FILE_TYPE_VIDEO)});
        list.add(new String[]{FILE_TYPE_AUDIO, getFileTypeName(loc, FILE_TYPE_AUDIO)});
        list.add(new String[]{FILE_TYPE_X_WORLD, getFileTypeName(loc, FILE_TYPE_X_WORLD)});
        list.add(new String[]{FILE_TYPE_DEFAULT, getFileTypeName(loc, FILE_TYPE_DEFAULT)});
        return list;
    }

    /**
     * 根据上传文件的contentType属性获取文件的文件类型（fileType）
     *
     * @param contentType
     * @return
     */
    public static String getFileContentType(String contentType) {
        if (contentType.startsWith("text")) {
            return FILE_TYPE_TEXT;
        }
        if (contentType.startsWith("image")) {
            return FILE_TYPE_IMAGE;
        }
        if (contentType.startsWith("application")) {
            return FILE_TYPE_APPLICATION;
        }
        if (contentType.startsWith("video")) {
            return FILE_TYPE_VIDEO;
        }
        if (contentType.startsWith("audio")) {
            return FILE_TYPE_AUDIO;
        }
        if (contentType.startsWith("x-world")) {
            return FILE_TYPE_X_WORLD;
        }
        return FILE_TYPE_DEFAULT;
    }

    /**
     * 清空栏目名称缓存
     */
    public static void clearFileCategoryNameMap() {
        if (categoryNameMap != null)
            categoryNameMap.clear();
    }

    public static Map<String, FileType> getFileExtTypeMapping() {
        return fileExtTypeMapping;
    }

    /**
     * 根据文件后缀名取得文件所属类型
     *
     * @param ext
     * @return
     */
    public static FileType getFileType(String ext) {
        FileType ft = fileExtTypeMapping.get(ext);
        if (ft == null) {
            ft = FileType.OTHER;
        }
        return ft;
    }

    /**
     * 是否为图像类型
     *
     * @param fileName
     * @return
     */
    public static boolean isImageTypeByName(String fileName) {
        return isImageType(getExtension(fileName));
    }

    /**
     * 是否为图像类型
     *
     * @param ext
     * @return
     */
    public static boolean isImageType(String ext) {
        FileType fileType = getFileType(ext);
        if (fileType == FileType.IMAGE) {
            return true;
        }
        return false;
    }

    /**
     * 文件类型
     *
     * @author XFB
     */
    public enum FileType {
        IMAGE("图片", "IMAGE"), VIDEO("视频", "VIDEO"), DOCUMENT("文档", "DOCUMENT"), AUDIO("音频", "AUDIO"), OTHER("其他", "OTHER");
        private String name = null;
        private String value = null;

        private FileType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

    }

}