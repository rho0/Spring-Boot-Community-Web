package com.web.controller;


import com.web.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/board") /** API 경로 지정 */
public class BoardController {

    @Autowired
    BoardService boardService;  /** boardService 의존성을 주입해야하므로 @Autowired를 사용 */

    @GetMapping({"", "/"}) /** 매핑경로를 중괄호를 사용하여 여러개를 받을 수 있음 */
    /** @RequestParam:
     * idx 파라미터를 필수로 받음.
     * 만약 바인딩할 값이 없으면 기본값'0'으로 설정됨.
     * findBoardByIdx(idx)로 조회시 idx값을 '0'으로 조회하면 board 값은 null 값으로 반환됨.*/
    public String board(@RequestParam(value = "idx", defaultValue = "0") Long idx, Model model) {
        model.addAttribute("board", boardService.findBoardByIdx(idx));
        return "/board/form";
    }

    @GetMapping("/list")
    /** @PageableDefault :
     * PageableDefault 어노테이션의 파라미터인 size, sort, direction 등을 사용하여 페이징 처리에 대한 규약을 정의 할 수 있다. */
    public String list(@PageableDefault Pageable pageable, Model model) {
        model.addAttribute("boardList", boardService.findBoardlist(pageable));
        return "/board/list"; /** src/resources/templates를 기준으로 데이터를 바인딩할 타깃의 뷰 경로를 지정 */
    }

}
