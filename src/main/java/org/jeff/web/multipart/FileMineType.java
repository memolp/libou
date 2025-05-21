package org.jeff.web.multipart;

import java.util.HashMap;

public enum FileMineType
{
    text(".txt", "text/plain"),
    json(".json", "text/json"),
    html(".html", "text/html"),
    css(".css", "text/css"),
    javascript(".js", "text/javascript"),
    png(".png", "image/png"),
    jpeg(".jpeg", "image/jpeg"),
    jpg(".jpg", "image/jpeg"),
    gif(".gif","image/gif"),
    svg(".svg", "image/svg+xml"),
    ico(".ico", "image/ico"),
    ttf(".ttf", "text/font"),
    woff(".woff", "text/font"),
    woff2(".woff2", "text/font"),
    mp4(".mp4", "video/mp4"),
    mp3(".mp3", "audio/mp3"),
    mpeg(".mpeg", "audio/mpeg"),
    mpeg2(".mpeg2", "audio/mpeg"),
    mpeg3(".mpeg3", "audio/mpeg"),
    mpeg4(".mpeg4", "audio/mpeg"),
    mp4v(".mp4v", "video/mp4"),
    mp4a(".mp4a", "audio/mp4"),
    avi(".avc", "video/x-mplayer2"),
    xls(".xls", "application/xls"),
    xlsx(".xlsx", "application/xlsx"),
    doc(".doc", "application/doc"),
    docx(".docx", "application/docx"),
    pdf(".pdf", "application/pdf"),
    zip(".zip", "application/zip"),
    rar(".rar", "application/rar"),
    tar(".tar", "application/tar"),
    gzip(".gz", "application/gzip"),

    ;
    private final String ext;
    private final String type;
    FileMineType(String ext, String type)
    {
        this.ext = ext;
        this.type = type;
    }
    private static final HashMap<String, String> _mineTypes = new HashMap<>();
    public static String file_ext(String filename)
    {
        int lastDot = filename.lastIndexOf('.');
        int lastSlash = Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
        if (lastDot > lastSlash) {
            return filename.substring(lastDot);
        } else {
            return ""; // 无扩展名
        }
    }
    public static String from(String filename)
    {
        String ext = file_ext(filename);
        return _mineTypes.getOrDefault(ext, "application/octet-stream");
    }
    static
    {
        for (FileMineType type : values())
        {
            _mineTypes.put(type.ext, type.type);
        }
    }
}
