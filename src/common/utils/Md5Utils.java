package common.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class Md5Utils
{

    private static byte[] md5(String s)
    {
        MessageDigest algorithm;
        try
        {
            algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(s.getBytes("UTF-8"));
            byte[] messageDigest = algorithm.digest();
            return messageDigest;
        }
        catch (Exception e)
        {
            // //logger.error("MD5 Error...", e);
        }
        return null;
    }

    private static final String toHex(byte hash[])
    {
        if (hash == null)
        {
            return null;
        }
        StringBuffer buf = new StringBuffer(hash.length * 2);
        int i;

        for (i = 0; i < hash.length; i++)
        {
            if ((hash[i] & 0xff) < 0x10)
            {
                buf.append("0");
            }
            buf.append(Long.toString(hash[i] & 0xff, 16));
        }
        return buf.toString();
    }

    public static String hash(String s)
    {
        try
        {
            return new String(toHex(md5(s)).getBytes("UTF-8"), "UTF-8");
        }
        catch (Exception e)
        {
            // //logger.error("not supported charset...{}", e);
            return s;
        }
    }

    public static String getMD5(File file)
    {

        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        FileInputStream fileInputStream = null;
        DigestInputStream digestInputStream = null;
        try
        {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            fileInputStream = new FileInputStream(file);
            digestInputStream = new DigestInputStream(fileInputStream,
                    messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0)
            {
                ;
            }
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return toHex(resultByteArray);
        }
        catch (NoSuchAlgorithmException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        catch (FileNotFoundException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        catch (IOException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        finally
        {
            try
            {
                if (digestInputStream != null)
                {
                    digestInputStream.close();
                }
            }
            catch (Exception e)
            {
                // logger.error(e.getMessage(), e);
            }
            try
            {
                if (fileInputStream != null)
                {
                    fileInputStream.close();
                }
            }
            catch (Exception e)
            {
                // logger.error(e.getMessage(), e);
            }
        }
    }

    public static String getMD5(InputStream input)
    {

        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        DigestInputStream digestInputStream = null;
        try
        {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 使用DigestInputStream
            digestInputStream = new DigestInputStream(input, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0)
            {
                ;
            }
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return toHex(resultByteArray);
        }
        catch (NoSuchAlgorithmException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        catch (FileNotFoundException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        catch (IOException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        finally
        {
            try
            {
                if (digestInputStream != null)
                {
                    digestInputStream.close();
                }
            }
            catch (Exception e)
            {
                // logger.error(e.getMessage(), e);
            }
        }
    }

    public static String getMD5(byte[] bytes)
    {

        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        ByteArrayInputStream input = null;
        DigestInputStream digestInputStream = null;
        try
        {
            // 拿到一个MD5转换器（同样，这里可以换成SHA1）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 字节输入流
            input = new ByteArrayInputStream(bytes);
            // 使用DigestInputStream
            digestInputStream = new DigestInputStream(input, messageDigest);
            // read的过程中进行MD5处理，直到读完文件
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0)
            {
                ;
            }
            // 获取最终的MessageDigest
            messageDigest = digestInputStream.getMessageDigest();
            // 拿到结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 同样，把字节数组转换成字符串
            return toHex(resultByteArray);
        }
        catch (NoSuchAlgorithmException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        catch (FileNotFoundException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        catch (IOException e)
        {
            // logger.error(e.getMessage(), e);
            return null;
        }
        finally
        {
            try
            {
                if (digestInputStream != null)
                {
                    digestInputStream.close();
                }
            }
            catch (Exception e)
            {
                // logger.error(e.getMessage(), e);
            }
            try
            {
                if (input != null)
                {
                    input.close();
                }
            }
            catch (Exception e2)
            {
                // logger.error(e2.getMessage(), e2);
            }
        }
    }

    public static String getMD52(File file)
    {
        FileInputStream fis = null;
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            fis = new FileInputStream(file);
            int bufferSize = 256 * 1024;
            byte[] buffer = new byte[bufferSize];
            int length = -1;
            while ((length = fis.read(buffer)) != -1)
            {
                md.update(buffer, 0, length);
            }
            byte[] b = md.digest();
            return toHex(b);
        }
        catch (Exception ex)
        {
            return null;
        }
        finally
        {
            try
            {
                if (fis != null)
                {
                    fis.close();
                }
            }
            catch (IOException ex)
            {
                // logger.error(ex.getMessage(), ex);
            }
        }

    }

}
