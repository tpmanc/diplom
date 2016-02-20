package models;

import db.Database2;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Created by tpmanc on 20.02.16.
 */
public class Tree {
    private int id;
    private int lft;
    private int rgt;
    private int categoryId;
    private int level;

    /**
     *
     * @param rgt Правый ключ родителя или максимальный правый ключ + 1, если родителя нет
     * @param level уровень родителя или 0, если родителя нет
     * @return
     */
    public static boolean addElem(int rgt, int level, int categoryId) {
        String sql1 = "UPDATE tree SET rgt = rgt + 2, lft = IF(lft > :rgt, lft + 2, lft) WHERE rgt >= :rgt";
        String sql2 = "INSERT INTO tree SET lft = :rgt, rgt = :rgt + 1, level = :level + 1, categoryId = :categoryId";
        JdbcTemplate template = new JdbcTemplate(Database2.getInstance().getBds());
        template.execute(sql1);
        template.execute(sql2);
        return true;
    }
}
