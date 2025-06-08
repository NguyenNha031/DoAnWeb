package com.nhom10.doanmonhoc.controller;

import com.nhom10.doanmonhoc.enums.Status;
import com.nhom10.doanmonhoc.model.*;
import com.nhom10.doanmonhoc.repository.*;
import com.nhom10.doanmonhoc.service.*;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
    private String getWritableImageFolder() {
        String folderPath;
        if (System.getenv("RENDER") != null) {
            folderPath = "/tmp/images/";
        } else {
            folderPath = getWritableImageFolder();

        }

        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folderPath;
    }
    private void injectCommonModelAttributes(Model model, Long idSite) {
        Site site = siteRepository.findById(idSite).orElseThrow();
        model.addAttribute("site", site);

        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(idSite);
        model.addAttribute("menus", menus);

        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Menu menu : menus) {
            List<Page> pages = pageRepository.findPublishedPagesByIdMenuOrderByCreatedAt(menu.getIdMenu(), Status.Published);
            if (!pages.isEmpty()) {
                menuToPageMap.put(menu.getIdMenu(), pages.get(0).getIdPage());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);
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
            String logoPath = getWritableImageFolder() + "Logo.png";

            File logoFile = new File(logoPath);
            BufferedImage img = null;
            if (logoFile.exists()) {
                img = ImageIO.read(logoFile);
                if (img != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(img, "png", baos);
                    byte[] b = baos.toByteArray();
                    Site site = new Site();
                    site.setName("My Site");
                    site.setLogo(b);
                    siteService.insertSiteNative(site);
                }
            } else {
                System.err.println("⚠️ Không tìm thấy file Logo.png tại: " + logoPath);
            }
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
    public String editSite(@PathVariable("id") Long id, Model model) {
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();

        // Load banner từ DB
        List<Banner> banners = bannerRepository.findByIdSiteOrderByIdBannerAsc(site.getIdSite());
        List<Map<String, String>> bannerList = new ArrayList<>();
        for (Banner b : banners) {
            Map<String, String> bannerMap = new HashMap<>();
            bannerMap.put("image", "/image/banner/" + b.getIdBanner());
            bannerMap.put("tooltip", b.getMota());
            bannerList.add(bannerMap);
        }

        model.addAttribute("bannerList", bannerList);
        model.addAttribute("site", site);

        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(site.getIdSite());
        model.addAttribute("menus", menus);

        Map<Long, Long> menuToPageMap = new HashMap<>();
        for (Menu menu : menus) {
            for (Page p : pageRepository.findPublishedPagesByIdMenuOrderByCreatedAt(menu.getIdMenu(), Status.Published)) {
                menuToPageMap.put(menu.getIdMenu(), p.getIdPage());
            }
        }
        model.addAttribute("menuToPageMap", menuToPageMap);

        // Tin tức nổi bật
        List<Post> allPosts = postRepository.findPublishedPostsByIdSiteOrderByPined(site.getIdSite(), Status.Published);
        List<Map<String, String>> posts = new ArrayList<>();
        if (!allPosts.isEmpty()) {
            Post p = allPosts.get(0);
            Map<String, String> postMap = new HashMap<>();
            postMap.put("id", String.valueOf(p.getIdPost()));
            postMap.put("title", p.getTitle());
            List<Block> blocks = blockRepository.findByIdPost(p.getIdPost());
            postMap.put("image", extractImageFromBlocks(blocks));
            postMap.put("tooltip", p.getMota());
            postMap.put("timeAgo", getTimeAgo(p.getCreatedAt()));
            postMap.put("link", "/post/" + p.getIdPost());
            posts.add(postMap);
        }
        model.addAttribute("posts", posts);

        return "Backend/editSite";
    }

    @PostMapping("/editsite/{id}")
    public String editSites(
            @PathVariable("id") Long id,
            @RequestParam(value = "logo", required = false) MultipartFile logo,
            @RequestParam(value = "banner", required = false) MultipartFile[] banners,
            @RequestParam(value = "btn-save", required = false) String btnSave
    ) throws IOException {

        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();

        if (btnSave != null) {
            // Logo
            if (logo != null && !logo.isEmpty()) {
                site.setLogo(logo.getBytes());
                siteService.editSite(site);
            }

            // Banner
            if (banners != null && banners.length > 0) {
                bannerService.deleteBanner(site.getIdSite());
                for (MultipartFile b : banners) {
                    if (b != null && !b.isEmpty()) {
                        Banner newBanner = new Banner();
                        newBanner.setIdSite(site.getIdSite());
                        newBanner.setMota("Ảnh banner/slider");
                        newBanner.setImage(b.getBytes());
                        bannerService.insertNativeBanner(newBanner);
                    }
                }
            }
        }

        return "redirect:/editsite/" + id;
    }

    @GetMapping("/image/banner/{id}")
    public ResponseEntity<byte[]> getBanner(@PathVariable Long id) {
        Optional<Banner> bannerOpt = bannerRepository.findById(id);
        if (bannerOpt.isEmpty() || bannerOpt.get().getImage() == null) {
            return ResponseEntity.notFound().build();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        return new ResponseEntity<>(bannerOpt.get().getImage(), headers, HttpStatus.OK);
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
        if (createdAt == null) return "Không rõ thời gian";
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
            post.setCreatedBy(1l);
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


    @GetMapping("/allpages/{id}")
    public String allPages(@PathVariable("id") Long id, Model model) {
        injectCommonModelAttributes(model,id);
        Optional<Site> siteOpt = siteRepository.findById(id);
        if (siteOpt.isEmpty()) return "redirect:/";

        Site site = siteOpt.get();
        model.addAttribute("site", site);

        List<Page> pages = pageRepository.findAll();
        model.addAttribute("pages", pages);

        List<Menu> menus = menuRepository.findByIdSiteOrderByIdMenuAsc(site.getIdSite());
        model.addAttribute("menus", menus);

        model.addAttribute("site", site);
        model.addAttribute("menus", menus);
        return "Backend/allPages";
    }


}