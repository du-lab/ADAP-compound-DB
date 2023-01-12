package org.dulab.adapcompounddb.site.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpectrumDB {

    private static final Logger LOG = LoggerFactory.getLogger(SpectrumDB.class);

    public static void selectSpectrum() {
        ConnectionPool pool = ConnectionPool.getInstance();
        Connection connection = pool.getConnection();

        PreparedStatement ps = null;
        ResultSet rs = null;
        String query = "SELECT * FROM Spectrum WHERE spectrumId = ?";

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, 0);
            rs = ps.executeQuery();
        }
        catch (SQLException e) {
            LOG.error(e.getMessage());
        }
        finally {
            pool.freeConnection(connection);
        }
    }

}
