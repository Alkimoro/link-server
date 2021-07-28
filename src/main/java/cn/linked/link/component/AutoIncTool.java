package cn.linked.link.component;

import cn.linked.link.entity.AutoInc;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class AutoIncTool {

    public static final String KEY_USER = "user";
    public static final String KEY_CHAT_GROUP = "chat_group";

    public static final Map<String, Long> autoIncOffsetMap = new HashMap<>();
    /**
     *  每个Key对应的Value 定下就不能修改 不然会造成生成ID不连续或有重复的ID出现
     *      默认ID都是从1开始的 即 offset 为 0
     * */
    static {
        autoIncOffsetMap.put("chat_group", 100000L);
        autoIncOffsetMap.put("user", 100000L);
    }

    @Resource(name = "autoIncMongoTemplate")
    private MongoTemplate autoIncMongoTemplate;

    /**
     *  使curSequenceNumber字段原子增加
     *  @return 增加后的值
     * */
    public Long increase(String key) {
        Query query = new Query(Criteria.where("key").is(key));
        Update update = new Update();
        update.inc("curSequenceNumber", 1);
        FindAndModifyOptions options = new FindAndModifyOptions();
        // true 先查询 如果没有符合条件的 会执行插入 插入的值是查询值 ＋ 更新值
        options.upsert(true);
        // 返回当前最新值
        options.returnNew(true);
        Long offset = autoIncOffsetMap.getOrDefault(key, 0L);
        return Objects.requireNonNull(autoIncMongoTemplate.findAndModify(query, update, options,
                AutoInc.class)).getCurSequenceNumber() + offset;
    }

}
