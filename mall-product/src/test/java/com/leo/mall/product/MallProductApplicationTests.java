package com.leo.mall.product;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.leo.mall.product.entity.BrandEntity;
import com.leo.mall.product.service.BrandService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest
class MallProductApplicationTests {

    @Autowired
    BrandService brandService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Test
    void redissonClient() {

    }

    @Test
    void testRedisTemplate() {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        ops.set("hello","world_" + UUID.randomUUID().toString());

        String hello = ops.get("hello");
        System.out.println(hello);
    }

    @Test
    void contextLoads() {
        BrandEntity entity = new BrandEntity();
        entity.setName("华为");
        brandService.save(entity);
        System.out.println("保存成功");
        List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
        list.forEach(e -> {
            System.out.println(e);
        });
    }

    @Test
    public void testUpload() {
        // yourEndpoint填写Bucket所在地域对应的Endpoint。以华东1（杭州）为例，Endpoint填写为https://oss-cn-hangzhou.aliyuncs.com。
//        String endpoint = "oss-cn-shanghai.aliyuncs.com";
        // 阿里云账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM用户进行API访问或日常运维，请登录RAM控制台创建RAM用户。
//        String accessKeyId = "LTAI5tPQh2CNMWGUkzT8rQrG";
//        String accessKeySecret = "rixplaCbp38TMXM4BrVhX8qeSh8ibS";

        // 创建OSSClient实例。
//        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        // 填写本地文件的完整路径。如果未指定本地路径，则默认从示例程序所属项目对应本地路径中上传文件流。
//        InputStream inputStream = new FileInputStream("C:\\Users\\A\\Pictures\\63e862164165f483.jpg");
        // 填写Bucket名称和Object完整路径。Object完整路径中不能包含Bucket名称。
//        ossClient.putObject("mall-hello-leo", "63e862164165f483.jpg", inputStream);

        // 关闭OSSClient。
//        ossClient.shutdown();
    }

}
