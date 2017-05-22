package cn.alone.controller;

import cn.alone.pojo.Blog;
import cn.alone.pojo.User;
import cn.alone.pojo.dto.BlogDTO;
import cn.alone.services.IBlogService;
import cn.alone.utils.PageUtil;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

/**
 * Created by RojerAlone on 2017/4/9.
 */
@Controller
@RequestMapping(value = "/blog")
public class BlogController  extends AbstractController{

    @Autowired
    private IBlogService blogService;

    @RequestMapping(value = "write", method = RequestMethod.GET)
    public String write() {
        return "editor";
    }

    @RequestMapping(value = "write", method = RequestMethod.POST)
    public String write(Blog blog) {
//        blog.setUid((User)this.getSession().getAttribute("user").getUid());
        blog.setUid(1);
        // 如果是新文章，写入
        if (blog.getBid() == null) {
            if (blogService.write(blog) == 1) {
                return "redirect:" + blog.getBid();
            }
        } else {
            if (blogService.update(blog)) {
                return "redirect:" + blog.getBid();
            }
        }
        blog.setUid(1);

        return "editor";
    }
    @RequestMapping(value = "delete", method = RequestMethod.DELETE)
    public String delete(int bid) {
        blogService.delBlog(bid);
        return "redirect:" + this.getRequest().getHeader("Referer");
    }

    @RequestMapping(value = "all/{pageIndex}", method = RequestMethod.GET)
    public String getAll(@PathVariable Integer pageIndex) {
        Pair<PageUtil, List<BlogDTO>> pageBlogPair;
        if (pageIndex == null) {
            pageBlogPair = blogService.getByPage(1);
        } else {
            pageBlogPair = blogService.getByPage(1);
        }
        this.getModel().addAttribute("page", pageBlogPair.getValue0());
        this.getModel().addAttribute("blogs", pageBlogPair.getValue1());
        return "bloglist";
    }

    @RequestMapping(value = "{bid}", method = RequestMethod.GET)
    public String getBlog(@PathVariable Integer bid) {
        BlogDTO blog;
        if ((blog = blogService.selectById(bid)) == null) {
            return "ErrorCode/ErrorCode404";
        }
        this.getModel().addAttribute("blog", blog);
        return "blog";
    }

    @RequestMapping(value = "update", method = RequestMethod.POST)
    public String update(Blog blog) {
        if (blogService.update(blog)) {
            return "redirect:/blog/" + blog.getBid();
        }
        return "redirect:" + edit(blog.getBid());
    }

    @RequestMapping(value = "edit", method = RequestMethod.GET)
    public String edit(int bid) {
        BlogDTO blog;
        if ((blog = blogService.selectById(bid)) != null) {
            this.getModel().addAttribute("blog", blog.getBlog());
        }
        return "editor";
    }

    // 需要jackson包，否则出现406错误
//    @RequestMapping(value = "hotblogs",
//            method = RequestMethod.GET,
//            produces = {"application/json;charset=UTF-8"})
//    @ResponseBody
//    public List<Blog> getHotBlogs() {
//        return blogService.get();
//    }

}
