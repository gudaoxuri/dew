package com.trc.test.dewutil.controller;

import com.ecfront.dew.common.Resp;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consumer/")
public class UtilController {

    private Consumer localConsumer;

    @PostMapping("save")
    public Resp<Consumer> save(@RequestBody Consumer consumer){
        localConsumer =consumer;
        return Resp.success(consumer);
    }

    @GetMapping("get")
    public Resp<Consumer>  get(@RequestParam("id") String id){
        return Resp.success(localConsumer);
    }

    @PutMapping("update")
    public Resp<Consumer> update(@RequestBody Consumer consumer){
        return Resp.success(consumer);
    }

    @DeleteMapping("delete")
    public Resp<Consumer> delete(@RequestParam("id")String id){
        return Resp.success(localConsumer);
    }

    public static class Consumer{

        private String id;

        private String name;

        private String password;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
