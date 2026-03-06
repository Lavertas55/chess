package dataaccess;

import dataaccess.exception.DataException;

public interface DataAccessObject {
    void clear() throws DataException;
}
