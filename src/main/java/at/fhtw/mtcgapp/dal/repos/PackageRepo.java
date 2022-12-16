package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.CardInfoUser;
import at.fhtw.mtcgapp.model.Package;
import at.fhtw.mtcgapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public void createCard(Card card) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO card (cid, name, damage, fk_pid) VALUES (?, ?, ?, ?)")) {
            preparedStatement.setString(1, card.getCid());
            preparedStatement.setString(2, card.getName());
            preparedStatement.setInt(3, card.getDamage());
            preparedStatement.setInt(4, card.getFk_pid());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            if(sqlException.getErrorCode() == 0) {
                throw new DataAccessException("duplicate card");
            }
            throw new DataAccessException("createCard error");
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

    public void acquireCardByPid(Package cardPackage, User user) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET fk_uid = (SELECT uid FROM \"user\" WHERE username = ?) WHERE fk_pid = ?")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, cardPackage.getPid());
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("acquireCardByPid");
        }
    }

    public List<CardInfoUser> getCardsByPid(Package cardPackage) throws DataAccessException {
        List<CardInfoUser> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_pid = ?")) {
            preparedStatement.setInt(1, cardPackage.getPid());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new CardInfoUser(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3)
                ));
            }
            return cards;
        } catch (SQLException sqlException) {
            throw new DataAccessException("getCardsByPid error");
        }
    }

    public List<CardInfoUser> getCardsByUid(User user) throws DataAccessException {
        List<CardInfoUser> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_uid = ?")) {
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new CardInfoUser(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3)
                ));
            }
            return cards;
        } catch (SQLException sqlException) {
            throw new DataAccessException("getCardsByUid error");
        }
    }

    public List<CardInfoUser> getCardsInDeck(User user) throws DataAccessException {
        List<CardInfoUser> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_uid = ? AND in_deck = true")) {
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new CardInfoUser(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3)
                ));
            }
            return cards;
        } catch (SQLException sqlException) {
            throw new DataAccessException("getCardsInDeck error");
        }
    }

    public void clearDeck(User user) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET in_deck = false WHERE fk_uid = ?")) {
            preparedStatement.setInt(1, user.getId());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            throw new DataAccessException("clearDeck error");
        }
    }

    public void putCardInDeck(String card, User user) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET in_deck = true WHERE cid = ? AND fk_uid = ?")) {
            preparedStatement.setString(1, card);
            preparedStatement.setInt(2, user.getId());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            throw new DataAccessException("putCardInDeck error");
        }
    }

    public void lockCard(String cid) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET is_locked = true WHERE cid = ?")) {
            preparedStatement.setString(1, cid);
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("lockCardError");
        }
    }

    public boolean checkCardIsEnabledToTrade(String cid) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT is_locked, in_deck FROM card WHERE cid = ?")) {
            preparedStatement.setString(1, cid);
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean isAvailable = false;
            if(resultSet.next()) {
                boolean isLocked = resultSet.getBoolean(1);
                boolean inDeck = resultSet.getBoolean(2);
                if (!isLocked && !inDeck) {
                    isAvailable = true;
                }

            }
            return isAvailable;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("checkCardIsLocked error");
        }
    }
}
