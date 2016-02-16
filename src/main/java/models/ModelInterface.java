package models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public interface ModelInterface {
    public boolean update() throws SQLException;
    public boolean add() throws SQLException;
    public boolean validate();
    public boolean delete() throws SQLException;
}
