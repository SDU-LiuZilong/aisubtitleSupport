package sdu.aisubtitle.support;

import com.alibaba.fastjson.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaProcess {

    /**
     * 根据时间字符串获得时长 格式：00:00:00,000
     *
     * @param time 时间字符串
     * @return 时长（秒为单位）
     * @author PY
     */
    private static double getTimelen(String time) {
        double sec = 0;
        String strs[] = time.split(":");
        if (strs[0].compareTo("0") > 0) {
            sec += Double.valueOf(strs[0]) * 60 * 60;
        }
        if (strs[1].compareTo("0") > 0) {
            sec += Double.valueOf(strs[1]) * 60;
        }
        if (strs[2].compareTo("0") > 0) {
            sec += Double.valueOf(strs[2]);
        }
        return sec;
    }

    /**
     * 获得视频或者音频文件的时长
     *
     * @param filePath 文件路径
     * @return 时长（秒为单位）
     * @author PY
     */
    public static double getTimeLen(String filePath) {
        List<String> commList = new ArrayList<>(Arrays.asList("ffmpeg", "-i", filePath));
        String res = ExecuteCommand.exec(commList);

        String regexDuration = "Duration: (.*?),";
        Pattern pattern = Pattern.compile(regexDuration);
        Matcher m = pattern.matcher(res);
        double timeLen = 0.0;
        if (m.find()) {
            timeLen = getTimelen(m.group(1));
        }
        return timeLen;
    }

    /**
     * 获得视频或音频的比特率
     *
     * @param filePath 文件路径
     * @return 比特率（kb/s为单位）
     * @author PY
     */
    public static int getBitrate(String filePath) {
        List<String> commList = new ArrayList<>(Arrays.asList("ffmpeg", "-i", filePath));
        String res = ExecuteCommand.exec(commList);

        String regexBitrate = ", bitrate: (\\d*) kb\\/s";
        Pattern pattern = Pattern.compile(regexBitrate);
        Matcher m = pattern.matcher(res);
        int bitrate = 0;
        if (m.find()) {
            bitrate = Integer.valueOf(m.group(1));
        }
        return bitrate;
    }

    /**
     * 获得文件大小
     *
     * @param filePath 文件路径
     * @return 文件大小（Byte为单位）
     * @author PY
     */
    public static long getSize(String filePath) {
        File f = new File(filePath);
        long size = 0;
        if (f.exists() && f.isFile()) {
            size = f.length();
        }
        return size;
    }

    /**
     * 获得文件的格式
     *
     * @param filePath 文件路径
     * @return 文件格式
     * @author PY
     */
    public static String getFormat(String filePath) {
        String[] temp = filePath.split("\\.");
        String format = temp[temp.length - 1];
        return format;
    }

    /**
     * 压缩视频
     *
     * @param videoPath           被压缩视频的路径
     * @param compressedVideoPath 压缩后视频的路径
     * @param b                   压缩后的比特率
     * @return 是否成功
     * @throws IOException
     * @throws InterruptedException
     * @author PY
     */
    public static Boolean compressVideo(final String videoPath, final String compressedVideoPath, final int b) throws IOException, InterruptedException {
        List<String> globals = new ArrayList<>();
        List<String> input1Opts = new ArrayList<>();
        Map<String, List<String>> inputs = new HashMap<>();
        inputs.put(videoPath, input1Opts);
        List<String> outputOpts = new ArrayList<>(Arrays.asList("-b", "" + b + "k", "-y"));
        Map<String, List<String>> outputs = new HashMap<>();
        outputs.put(compressedVideoPath, outputOpts);
        FFmpegJ ff = new FFmpegJ(globals, inputs, outputs);
        System.out.println(ff.cmd());
        return ff.run();
    }

    /**
     * 导出音频
     *
     * @param videoPath 视频路径
     * @param audioPath 音频路径
     * @return 是否成功
     * @throws IOException
     * @throws InterruptedException
     * @author PY
     */
    public static Boolean exportAudio(final String videoPath, final String audioPath) throws IOException, InterruptedException {
        List<String> globals = new ArrayList<>();
        List<String> input1Opts = new ArrayList<>();
        Map<String, List<String>> inputs = new HashMap<>();
        inputs.put(videoPath, input1Opts);
        List<String> outputOpts = new ArrayList<>(Arrays.asList("-vn", "-c:a", "copy", "-y"));
        Map<String, List<String>> outputs = new HashMap<>();
        outputs.put(audioPath, outputOpts);
        FFmpegJ ff = new FFmpegJ(globals, inputs, outputs);
        System.out.println(ff.cmd());
        return ff.run();
    }

    /**
     * 导入字幕
     *
     * @param videoPath             视频路径
     * @param subtitlePath          字幕路径
     * @param videoWithSubtitlePath 导入路径后的视频路径
     * @return 是否成功
     * @throws IOException
     * @throws InterruptedException
     * @author PY
     */
    public static Boolean importSubtitle(final String videoPath, final String subtitlePath, final String videoWithSubtitlePath) throws IOException, InterruptedException {
        List<String> globals = new ArrayList<>();
        List<String> input1Opts = new ArrayList<>();
        Map<String, List<String>> inputs = new HashMap<>();
        inputs.put(videoPath, input1Opts);
        List<String> outputOpts = new ArrayList<>(Arrays.asList("-vf", "subtitles=" + subtitlePath, "-y"));
        Map<String, List<String>> outputs = new HashMap<>();
        outputs.put(videoWithSubtitlePath, outputOpts);
        FFmpegJ ff = new FFmpegJ(globals, inputs, outputs);
        System.out.println(ff.cmd());
        return ff.run();
    }

}
