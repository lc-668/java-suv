package com.flyfire.tool.rest;

import com.flyfire.tool.util.vo.ApiRes;
import com.flyfire.tool.constant.SuvConst;
import com.flyfire.tool.model.SuvProgram;
import com.flyfire.tool.service.SuvService;
import com.flyfire.tool.vo.SuvProgramVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class SuvRest {

    @Autowired
    private SuvService suvService;

    @GetMapping("/suv/list")
    public ApiRes<List<SuvProgramVo>> list() {
        List<SuvProgramVo> outList = SuvConst.SUV_PROGRAMS.stream().map(item -> {
            SuvProgramVo vo = new SuvProgramVo();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).collect(Collectors.toList());
        return ApiRes.newSucces(outList);
    }

    @PostMapping("/suv/startProgram")
    public ApiRes<String> startProgram(String program) {
        try {
            SuvProgram suvProgram = SuvConst.SUV_PROGRAM_MAP.get(program);
            suvProgram.setRetryStart(false);
            suvService.startProgram(suvProgram);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiRes.fail(e.getMessage());
        }
        return ApiRes.newSucces("启动" + program);
    }

    @PostMapping("/suv/stopProgram")
    public ApiRes<String> stopProgram(String program) {
        try {
            suvService.stopProgram(program);
        } catch (Exception e) {
            e.printStackTrace();
            return ApiRes.fail(e.getMessage());
        }
        return ApiRes.newSucces("停止" + program);
    }

    @GetMapping("/suv/tail/{suvKey}")
    public void stream(HttpServletResponse response, @PathVariable String suvKey) throws Exception {
        // 设置响应头，告知浏览器这是一个PDF文件
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");
        SuvProgram suvProgram = SuvConst.SUV_PROGRAM_MAP.get(suvKey);
        // 假设你有一个方法可以生成PDF流
        /*InputStream inputStream = generatePdfStream();*/
        // 获取输出流
        OutputStream outputStream = response.getOutputStream();
        // 将输入流复制到输出流
        /*YLimitQueue<Byte> queue = suvProgram.getSuvProgramCmd().ps.queue;
        for (Byte b : queue.foAll()) {
            outputStream.write(b);
        }*/
        if (suvProgram.getSuvProgramCmd() == null) {
            outputStream.write(("请启动" + suvKey + "\r\n\n").getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        }
        suvProgram.ps.addSyncListener(outputStream);
       /* // 关闭流
        outputStream.flush();
        outputStream.close();*/
    }


}
