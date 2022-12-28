package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.exceptions.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.Package;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepo {

    private final UOW uow;

    public PackageRepo(UOW uow) {
        this.uow = uow;
    }

    public int createPackageAndGetId() throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO package DEFAULT VALUES RETURNING pid")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            int pid = 0;
            if (resultSet.next()) {
                pid = resultSet.getInt(1);
            }
            return pid;
        } catch (SQLException sqlException) {
            throw new DataAccessException("createPackageAndGetId error");
        }
    }

    public Package getPackage() throws DataAccessException {
        Package cardPackage = null;
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM package WHERE is_available = true")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                cardPackage = new Package(
                        resultSet.getInt(1),
                        resultSet.getInt(2),
                        resultSet.getBoolean(3)
                );
            }
            return cardPackage;
        } catch (SQLException sqlException) {
            throw new DataAccessException("getPackageError");
        }
    }

    public void makePackageUnavailable(Package cardPackage) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE package SET is_available = false WHERE pid = ?")) {
            preparedStatement.setInt(1, cardPackage.getPid());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            throw new DataAccessException("makePackageUnavailable error");
        }
    }
}
