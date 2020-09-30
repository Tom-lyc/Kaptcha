package cn.kgc.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.kaptcha.impl.DefaultKaptcha;

@Controller
public class KaptchaController {
	
	@Autowired
	DefaultKaptcha defaultKaptcha;
	
	
	@RequestMapping("/toindex")
	public String toIndex(){
		return "index";
	}
	
	
	/*  1.验证码工具
	 * 
	 * */
	@RequestMapping("/defaultKaptcha")
	public void defaultKaptcha(HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		 byte[] cap=null;		
		//ByteArrayOutputStream  对byte类型数据进行写入的类
		 ByteArrayOutputStream jpem=new ByteArrayOutputStream(); 
		 try{
			 //生成验证码的字符串并保存到session中
			 String createTxt=defaultKaptcha.createText();
			 request.getSession().setAttribute("rightCode",createTxt);
			 /* Image是一个抽象类,BufferedImage是其实现类
			  * 是一个带缓冲区的图像类，主要作用将一幅图片加载到内存中
			  * 图片只有加载到内存我们才能进行进一步的处理
			  * */
			 //使用生成的验证码的字符串返回一个BufferedImage对象
			 BufferedImage challenge=defaultKaptcha.createImage(createTxt);
		     /*BufferedImage---byte[]
		      * format:图片格式  
		      * out
		      * 
		      * */
			 ImageIO.write(challenge,"jpg",jpem);
		 }catch(Exception e){
			 e.printStackTrace();
			 return;
		 }
		  cap=jpem.toByteArray();
		  //控制不同浏览器不要缓存，一般这三个都写，做到浏览器版本的兼容
		  response.setHeader("Cache-Control","no-store");
		  response.setHeader("Pragma","no-cache");
		  response.setDateHeader("Expires",0);
		  //后台servlet的服务器返回到前台的数据类型
		  response.setContentType("image/jpeg");
		  //将一个二进制的数据写入响应的流
		  ServletOutputStream ream=response.getOutputStream();
		  ream.write(cap);
		  ream.flush();
		  ream.close();
		 
	}
	
	
	
	
	/*  校验验证码
	 * 
	 * */
	    @RequestMapping("/imgka")
	    public  ModelAndView imgka(HttpServletRequest request,HttpServletResponse response){
	    	
	    	ModelAndView ma=new ModelAndView();
	    	//获取验证码里字符串的值，根据session去获取
	        String rt=(String)request.getSession().getAttribute("rightCode");
	    	//获取自己在文本框里输入的验证码的值
	        String trys=  request.getParameter("tryCode");
	        //做判断
	        if(!rt.equals(trys)){
	        	ma.addObject("info","错误的验证码");
	        	ma.setViewName("index");
	        }else{
	        	ma.addObject("info","登录成功");
	        	ma.setViewName("success");
	        }
	    	
	    	return ma;
	    }

}
