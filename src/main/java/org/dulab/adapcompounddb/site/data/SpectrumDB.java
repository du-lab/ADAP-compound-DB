package org.dulab.adapcompounddb.site.data;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SpectrumDB {

    private static final Logger LOG = LogManager.getLogger(SpectrumDB.class);

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
            LOG.error(e);
        }
        finally {
            pool.freeConnection(connection);
        }
    }

}
