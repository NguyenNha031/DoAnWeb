package com.nhom10.doanmonhoc.controller;

import com.nhom10.doanmonhoc.enums.Status;
import com.nhom10.doanmonhoc.model.*;
import com.nhom10.doanmonhoc.repository.*;
import com.nhom10.doanmonhoc.service.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
;

@Controller
public class HomeController_Backend {
    @Autowired
    private PostService postService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private BlockService blockService;
    @Autowired
    private SiteRepository siteRepository;
    @Autowired
    private SiteService siteService;
    @Autowired
    private PageRepository pageRepository;
    @Autowired
    private BannerRepository bannerRepository;
    @Autowired
    private MenuRepository menuRepository;
    @Autowired
    private BlockRepository blockRepository;
    @Autowired
    private BannerService bannerService;

    private boolean isValidImageUrl(String url) {
        return url != null && url.matches("^https?://.*\\.(png|jpg|jpeg|gif|webp)$");
    }
    private String extractImageFromBlocks(List<Block> blocks) {
        String fallback = "https://xdcs.cdnchinhphu.vn/446259493575335936/2024/8/17/nhatrang1-17238902889991160055539.jpg";
        for (Block block : blocks) {
            if (block.getCode() != null && block.getCode().contains("<img")) {
                Matcher matcher = Pattern.compile("src\\s*=\\s*\"([^\"]+)\"").matcher(block.getCode());
                if (matcher.find()) {
                    String imgUrl = matcher.group(1);
                    if (isValidImageUrl(imgUrl)) {
                        return imgUrl;
                    }
                }
            }
        }
        return fallback;
    }

    @GetMapping("/")
    public String sites(Model model) {
        List<Map<String,String>> sites = new ArrayList<>();
        for (Site s: siteRepository.findAllByOrderByIdSiteAsc()){
            Map<String, String> siteMap = new HashMap<>();
            siteMap.put("site_name",s.getName());
            siteMap.put("last_published",getTimeAgo(s.getUpdatedAt()));
            siteMap.put("link","/site/"+s.getIdSite());
            siteMap.put("link_edit","/back/"+s.getIdSite());
            sites.add(siteMap);
        }
        model.addAttribute("sites", sites);
        return "Backend/allSites";
    }
    @PostMapping("/")
    public String allSites(Model model,@RequestParam(value = "btn-add",required = false) String cl) throws IOException {
        if(!Objects.equals(cl, null)) {
            String str_b = "";
            BufferedImage img = ImageIO.read(new File(System.getProperty("user.dir") + "/src/main/resources/images/Logo.png"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] b = baos.toByteArray();
            str_b = Arrays.toString(b);
            Site site = new Site();
            site.setName("My Site");
            site.setLogo(str_b);
            siteService.insertSiteNative(site);
        }
        List<Map<String,String>> sites = new ArrayList<>();
        for (Site s: siteRepository.findAllByOrderByIdSiteAsc()){
            Map<String, String> siteMap = new HashMap<>();
            siteMap.put("site_name",s.getName());
            siteMap.put("last_published",getTimeAgo(s.getUpdatedAt()));
            siteMap.put("link","/site/"+s.getIdSite());
            siteMap.put("link_edit","/back/"+s.getIdSite());
            sites.add(siteMap);
        }
        model.addAttribute("sites", sites);
        return "Backend/allSites";
    }
    @GetMapping("/editsite/{id}")
    public String editSite(@PathVariable("id") Long id, Model model) throws IOException {
        String folderPath = System.getProperty("user.dir") + "/src/main/resources/images/";
        Path folder = Paths.get(folderPath);
        for (File file : Objects.requireNonNull(folder.toFile().listFiles())) {
            if(file.delete()){
                System.out.println(file.getName());
            }
        }
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        String[] byteArray = (site.getLogo()).substring(1,(site.getLogo()).length()-2).toLowerCase().split(", ");
        byte[] imageBytes = new byte[byteArray.length];
        for (int i=0; i<byteArray.length; i++) {
            imageBytes[i] = Byte.parseByte(byteArray[i]);
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        ImageIO.write(bufferedImage,"png",new File(System.getProperty("user.dir") + "/src/main/resources/images/Logo.png"));
        List<Banner> banners = bannerRepository.findByIdSiteOrderByIdBannerAsc(site.getIdSite());
        List<Map<String, String>> bannerList = new ArrayList<>();
        for (Banner b : banners) {
            Map<String, String> bannerMap = new HashMap<>();
            String[] byteArray1 = (b.getImage()).substring(1,(b.getImage()).length()-2).toLowerCase().split(", ");
            byte[] imageBytes1 = new byte[byteArray1.length];
            for (int i=0; i<byteArray1.length; i++) {
                imageBytes1[i] = Byte.parseByte(byteArray1[i]);
            }
            ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(imageBytes1);
            BufferedImage bufferedImage1 = ImageIO.read(byteArrayInputStream1);
            ImageIO.write(bufferedImage1,"png",new File(System.getProperty("user.dir") + "/src/main/resources/images/SliBanner"+b.getIdBanner()+".png"));
            bannerMap.put("image","http://localhost:8888/contents/images/SliBanner"+b.getIdBanner()+".png");
            bannerMap.put("tooltip", b.getMota());
            bannerList.add(bannerMap);
        }
        model.addAttribute("bannerList", bannerList);
        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(site.getIdSite());
        model.addAttribute("menus", menus);
        model.addAttribute("site", site);
        List<Post> allPosts = postRepository.findPublishedPostsByIdSiteOrderByPined(site.getIdSite(), Status.Published);
        List<Map<String, String>> posts = new ArrayList<>();
        if (!allPosts.isEmpty()) {
            Post p = allPosts.get(0);
            Map<String, String> postMap = new HashMap<>();
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("title", p.getTitle());
            List<Block> blocks = blockRepository.findByIdPost(p.getIdPost());
            String imageUrl = extractImageFromBlocks(blocks);
            postMap.put("image", imageUrl);
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        Map<Long, Long> menuToPageMap = new HashMap<>();
        for(Menu menu : menus){
            for (Page p : pageRepository.findPublishedPagesByIdMenuOrderByCreatedAt(menu.getIdMenu(), Status.Published)) {
                menuToPageMap.put(menu.getIdMenu(), p.getIdPage());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);
        return "Backend/editSite";
    }
    @PostMapping("/editsite/{id}")
    public String editSites(
            @PathVariable("id") Long id,
            @RequestParam(value = "logo",required = false) MultipartFile logo,
            @RequestParam(value = "panner",required = false) MultipartFile[] panner,
            @RequestParam(value = "btn-save",required = false) String btnSave,
            Model model) throws IOException {
        String folderPath = System.getProperty("user.dir") + "/src/main/resources/images/";
        Path folder = Paths.get(folderPath);
        for (File file : Objects.requireNonNull(folder.toFile().listFiles())) {
            if(file.delete()){
                System.out.println(file.getName());
            }
        }
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        if(!Objects.equals(btnSave, null)){
            if(!logo.isEmpty()){
                String str_b = "";
                BufferedImage img = ImageIO.read(logo.getInputStream());
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(img, "png", baos);
                byte[] b = baos.toByteArray();
                str_b = Arrays.toString(b);
                site.setLogo(str_b);
                siteService.editSite(site);
            }
            if(panner.length>0){
                bannerService.deleteBanner(site.getIdSite());
                for(MultipartFile p : panner){
                    String str_b = "";
                    BufferedImage img = ImageIO.read(p.getInputStream());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", baos);
                    byte[] b = baos.toByteArray();
                    str_b = Arrays.toString(b);
                    Banner banner = new Banner();
                    banner.setIdSite(site.getIdSite());
                    banner.setMota("Ảnh banner/slider");
                    banner.setImage(str_b);
                    bannerService.insertNativeBanner(banner);
                }
            }
        }

        String[] byteArray = (site.getLogo()).substring(1,(site.getLogo()).length()-2).toLowerCase().split(", ");
        byte[] imageBytes = new byte[byteArray.length];
        for (int i=0; i<byteArray.length; i++) {
            imageBytes[i] = Byte.parseByte(byteArray[i]);
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(imageBytes);
        BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);
        ImageIO.write(bufferedImage,"png",new File(System.getProperty("user.dir") + "/src/main/resources/images/Logo.png"));
        List<Banner> banners = bannerRepository.findByIdSiteOrderByIdBannerAsc(site.getIdSite());
        List<Map<String, String>> bannerList = new ArrayList<>();
        for (Banner b : banners) {
            Map<String, String> bannerMap = new HashMap<>();
            String[] byteArray1 = (b.getImage()).substring(1,(b.getImage()).length()-2).toLowerCase().split(", ");
            byte[] imageBytes1 = new byte[byteArray1.length];
            for (int i=0; i<byteArray1.length; i++) {
                imageBytes1[i] = Byte.parseByte(byteArray1[i]);
            }
            ByteArrayInputStream byteArrayInputStream1 = new ByteArrayInputStream(imageBytes1);
            BufferedImage bufferedImage1 = ImageIO.read(byteArrayInputStream1);
            ImageIO.write(bufferedImage1,"png",new File(System.getProperty("user.dir") + "/src/main/resources/images/SliBanner"+b.getIdBanner()+".png"));
            bannerMap.put("image","http://localhost:8888/contents/images/SliBanner"+b.getIdBanner()+".png");
            bannerMap.put("tooltip", b.getMota());
            bannerList.add(bannerMap);
        }
        model.addAttribute("bannerList", bannerList);
        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(site.getIdSite());
        model.addAttribute("menus", menus);
        model.addAttribute("site", site);
        List<Post> allPosts = postRepository.findPublishedPostsByIdSiteOrderByPined(site.getIdSite(), Status.Published);
        List<Map<String, String>> posts = new ArrayList<>();
        if (!allPosts.isEmpty()) {
            Post p = allPosts.get(0);
            Map<String, String> postMap = new HashMap<>();
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("title", p.getTitle());
            List<Block> blocks = blockRepository.findByIdPost(p.getIdPost());
            String imageUrl = extractImageFromBlocks(blocks);
            postMap.put("image", imageUrl);
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        Map<Long, Long> menuToPageMap = new HashMap<>();
        for(Menu menu : menus){
            for (Page p : pageRepository.findPublishedPagesByIdMenuOrderByCreatedAt(menu.getIdMenu(), Status.Published)) {
                menuToPageMap.put(menu.getIdMenu(), p.getIdPage());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);

        return "Backend/editSite";
    }

    @GetMapping("/back/{id}")
    public String allPosts(@PathVariable("id") Long id,Model model) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        return "Backend/index";
    }
    private String getTimeAgo(LocalDateTime createdAt) {
        Duration duration = Duration.between(createdAt, LocalDateTime.now());
        if (duration.toMinutes() < 1) return "Vừa đăng";
        if (duration.toMinutes() < 60) return "Đã đăng " + duration.toMinutes() + " phút trước";
        if (duration.toHours() < 24) return "Đã đăng " + duration.toHours() + " giờ trước";
        return "Đã đăng " + duration.toDays() + " ngày trước";
    }
    @GetMapping("/allpost/{id}")
    public String allPost(@PathVariable("id") Long id,Model model) {

        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        List<Map<String, String>> posts = new ArrayList<>();
        for (Post p : postRepository.findPublishedPostsByIdSiteOrderByPined(id, Status.Published)) {
            Map<String, String> postMap = new HashMap<>();
            postMap.put("title", p.getTitle());
            postMap.put("image", "https://xdcs.cdnchinhphu.vn/446259493575335936/2024/8/17/nhatrang1-17238902889991160055539.jpg");
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);

        }
        model.addAttribute("posts", posts);
        return "Backend/allPosts";
    }
    @GetMapping("/addpost/{id}")
    public String addPost(@PathVariable("id") Long id,Model model) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        return "Backend/addPost";
    }

    @PostMapping("/addpost/{id}")
    public String addPostLinhTinh(@PathVariable("id") Long id,
                                  Model model,
                                  @RequestParam("block") List<String> block_content,
                                  @RequestParam("title") String title,
                                  @RequestParam(value = "btn-publish",required = false) String cl
    ) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);
        if(!Objects.equals(cl, null)){
            Post post = new Post();
            post.setTitle(title);
            post.setIdSite(site.getIdSite());
            post.setStatus(Status.Published);
            post.setMota("Web gi day?");
            post.setPined(false);
            post.setCreatedBy(6L);
            postService.insertPostNative(post);
            Post insertedPost = postRepository.findTopByOrderByIdPostDesc();
            for (String blockContent : block_content) {
                if(blockContent!=null && !blockContent.isEmpty()) {
                    Block block = new Block();
                    block.setCode(blockContent);
                    block.setIdPost(insertedPost.getIdPost());
                    block.setIdPage(null);
                    blockService.insertBlockNative(block);
                }
            }
        }
        return "Backend/addPost";
    }
}