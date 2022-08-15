package com.wukong;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By WuKong on 2022/8/15 7:16
 **/
public class Test {
    public static void main(String[] args) {

        List<String> paths = new ArrayList<>();

        getAllFilePaths(new File("E:\\war\\SSM构建"),paths);

        paths.forEach(file->{
            System.out.println(file.toString());
        });
    }


    public static int test() {
        int a  = 3;
        try {
           int b = 3/0;
        }catch (Exception e){
            return a;
        }finally {
            a = 4;
        }
        return a;
    }

    public static void test1() {
        // JDK新版的处理 IO数据流
        try (
                FileInputStream fileInputStream = new FileInputStream("E:\\blog.sql");
                BufferedInputStream bis = new BufferedInputStream(fileInputStream);
                FileOutputStream fileOutputStream = new FileOutputStream("E:\\war\\blog.sql");
                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);)
        {
            int size;
            byte[] buf = new byte[1024];
            while ((size = bis.read(buf)) != -1){
                bos.write(buf,0,size);
            }

            System.out.println("赋值完成！！");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //递归
    public static void getAllFilePaths(File filePath,List<String> paths){
        //找出某目录下的所有子目录以及子文件，并打印打控制台上
        //递归
        File[] files = filePath.listFiles();
        if (files == null){
            return;
        }
        for (File f :files){
            if (f.isDirectory()){
                paths.add(f.getPath());
                getAllFilePaths(f,paths);
            }else {
                paths.add(f.getPath());
            }
        }

    }
}
