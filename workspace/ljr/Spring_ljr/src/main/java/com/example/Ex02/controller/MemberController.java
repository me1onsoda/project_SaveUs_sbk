package com.example.Ex02.controller;

import com.example.Ex02.dto.MemberDto;
import com.example.Ex02.mapper.MemberMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class MemberController {

    @Autowired
    MemberMapper memberMapper;

    // 회원가입창 이동
    @RequestMapping("/minsert.mb")
    public String insert(@ModelAttribute("mDto") MemberDto mDto){

        return "member/memberInsert";
    }

    // 삽입처리
    @RequestMapping("/minsertProc.mb")
    public String insertProc(@ModelAttribute("mDto") @Valid MemberDto mDto, BindingResult br){

        String resultPage;
        if (br.hasErrors()) {
            resultPage = "member/memberInsert";
        } else {
            memberMapper.insertMember(mDto);
            resultPage = "redirect:/mlist.mb";
        }
        return resultPage;
    }

    // 수정 폼으로 이동
    @RequestMapping("/mupdate.mb")
    public String update(@RequestParam("id") String id, Model model,
                         @RequestParam(value = "whatColumn", required = false) String whatColumn,
                         @RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page",defaultValue = "1") int page
                         ){

        MemberDto mDto = memberMapper.findById(id);

        model.addAttribute("mDto",mDto);
        model.addAttribute("whatColumn",whatColumn);
        model.addAttribute("keyword",keyword);
        model.addAttribute("page",page);
        model.addAttribute("id",id);

        return "member/memberUpdate";
    }

    // 수정처리
    @PostMapping("/mupdateProc.mb")
    public String updateProc(@ModelAttribute("mDto") @Valid  MemberDto mDto, BindingResult br ,Model model,
                             @RequestParam(value = "whatColumn", required = false) String whatColumn,
                             @RequestParam(value = "keyword", required = false) String keyword,
                             @RequestParam(value = "page",defaultValue = "1") int page) {

        model.addAttribute("page",page);
        model.addAttribute("whatColumn",whatColumn);
        model.addAttribute("keyword",keyword);

        String resultPage;
        if (br.hasErrors()) {
            resultPage = "member/memberUpdate";
        } else {
            memberMapper.updateMember(mDto);
            String encodeKeyword = keyword != null ? URLEncoder.encode(keyword, StandardCharsets.UTF_8) : "";
            resultPage = "redirect:/myPage.mb?page="+page+"&whatColumn="+whatColumn+"&keyword="+encodeKeyword;
        }
        return resultPage;
    }

    @RequestMapping(value = "/mdelete.mb")
    public String delete(@RequestParam("id") String id, Model model,
                         @RequestParam(value = "whatColumn", required = false) String whatColumn,
                         @RequestParam(value = "keyword", required = false) String keyword,
                         @RequestParam(value = "page",defaultValue = "1") int page) {

        // 삭제 실행
        memberMapper.deleteMember(id);

        int limit=5;
        int offset= (page-1) * limit;

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("whatColumn", whatColumn);
        params.put("keyword", keyword);
        params.put("offset", offset);
        params.put("limit", limit);

        int totalCount = memberMapper.getCount(params);
        if(totalCount % limit == 0){
            page = page-1;
        }

        model.addAttribute("page",page);
        model.addAttribute("whatColumn",whatColumn);
        model.addAttribute("keyword",keyword);

        String resultPage;
        String encodeKeyword = keyword != null ? URLEncoder.encode(keyword, StandardCharsets.UTF_8) : "";
        resultPage = "redirect:/myPage.mb?page="+page+"&whatColumn="+whatColumn+"&keyword="+encodeKeyword;
        return resultPage;
    }

    @RequestMapping(value="/checkId.mb")
    @ResponseBody
    public String checkDuplicate(@RequestParam("id") String id){
        System.out.println("checkId.mb");
        int count = memberMapper.selectCountById(id); // kim
        System.out.println("count:" + count);
        String result ="";
        if(count > 0){
            result = "duplicate";
        }else{
            result = "available";
        }
        return result;
    }

    // home.html => 로그인
    @GetMapping(value = "/login.mb")
    public String login(){

        return "member/memberLoginForm";
    }

    // 회원가입 => 로그인
    @PostMapping(value ="/login.mb")
    public String loginProc(MemberDto mDto, HttpServletRequest request, HttpServletResponse response, HttpSession session) throws IOException { // id , password

        // 사용자가 입력한 id를 이용해서 DB에서 해당 회원 정보 가져오기
        MemberDto member= memberMapper.findById(mDto.getId());
        System.out.println("member : "+ member);
        System.out.println("destination : " + session.getAttribute("destination"));

        response.setContentType("text/html;charset=UTF-8");

        PrintWriter pw;
        pw = response.getWriter();

        // [조건 1] 회원이 존재하지 않는 경우
        if(member == null){
            System.out.println("존재하지 않는 회원");
            pw.println("<script type='text/javascript'>");
            pw.println("alert('해당 아이디가 존재하지 않습니다.')");
            pw.println("</script>");
            return "member/memberLoginForm";
        }
        else{
            // [조건 2] 회원이 존재하는 경우
            System.out.println("존재하는 회원");

            // 비밀번호가 일치하는지 확인
            if(member.getPassword().equals(mDto.getPassword())){
                // 비밀번호가 맞으면 (로그인 성공) ==> 로그인 성공해야 세션이 설정됨
                session.setAttribute("loginInfo", member);
                String destination = (String)session.getAttribute("destination");
                if(session.getAttribute("destination")==null){
                    return "home";
                }else {
                    return destination;
                }
            }else{
                // 비밀번호가 틀리면 (로그인 실패)
                pw.println("<script type='text/javascript'>");
                pw.println("alert('비밀번호가 일치안함')");
                pw.println("</script>");
                pw.flush();
                return "member/memberLoginForm";
            }
        }
    }

    @GetMapping(value = "/logout.mb")
    public String logout(HttpSession session){
        session.invalidate(); // 세션 강제종료
        return "redirect:/";
    }
}
