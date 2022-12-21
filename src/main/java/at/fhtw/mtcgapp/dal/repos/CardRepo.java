package at.fhtw.mtcgapp.dal.repos;

import at.fhtw.mtcgapp.dal.DataAccessException;
import at.fhtw.mtcgapp.dal.UOW;
import at.fhtw.mtcgapp.model.Card;
import at.fhtw.mtcgapp.model.userview.CardUserView;
import at.fhtw.mtcgapp.model.Package;
import at.fhtw.mtcgapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CardRepo {

    private final UOW uow;

    public CardRepo(UOW uow) {
        this.uow = uow;
    }

    public void createCard(Card card) throws DataAccessException {
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("INSERT INTO card (cid, name, damage, type, element, fk_pid) VALUES (?, ?, ?, ?, ?, ?)")) {
            preparedStatement.setString(1, card.getCid());
            preparedStatement.setString(2, card.getName());
            preparedStatement.setInt(3, card.getDamage());
            preparedStatement.setString(4, card.getType());
            preparedStatement.setString(5, card.getElement());
            preparedStatement.setInt(6, card.getFk_pid());
            preparedStatement.execute();
        } catch (SQLException sqlException) {
            if(sqlException.getErrorCode() == 0) {
                throw new DataAccessException("duplicate card");
            }
            throw new DataAccessException("createCard error");
        }
    }

    public void acquireCardsByPid(Package cardPackage, User user) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET fk_uid = (SELECT uid FROM \"user\" WHERE username = ?) WHERE fk_pid = ?")) {
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setInt(2, cardPackage.getPid());
            preparedStatement.execute();
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("acquireCardByPid");
        }
    }

    public List<CardUserView> getCardsByPid(Package cardPackage) throws DataAccessException {
        List<CardUserView> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_pid = ?")) {
            preparedStatement.setInt(1, cardPackage.getPid());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new CardUserView(
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

    public List<CardUserView> getCardsByUid(User user) throws DataAccessException {
        List<CardUserView> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_uid = ?")) {
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new CardUserView(
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

    public List<CardUserView> getCardsInDeckUserView(User user) throws DataAccessException {
        List<CardUserView> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_uid = ? AND in_deck = true")) {
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new CardUserView(
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

    public List<Card> getCardsInDeckUser(User user) throws DataAccessException {
        List<Card> cards = new ArrayList<>();
        try (PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE fk_uid = ? AND in_deck = true")) {
            preparedStatement.setInt(1, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                cards.add(new Card(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getBoolean(6),
                        resultSet.getBoolean(7),
                        resultSet.getInt(8),
                        resultSet.getInt(9))
                );
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

    public Card getCardByIdFromUser(User user, String id) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("SELECT * FROM card WHERE cid = ? and fk_uid = ?")) {
            preparedStatement.setString(1, id);
            preparedStatement.setInt(2, user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            Card card = null;
            while(resultSet.next()) {
                card = new Card(
                        resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3),
                        resultSet.getString(4),
                        resultSet.getString(5),
                        resultSet.getBoolean(6),
                        resultSet.getBoolean(7),
                        resultSet.getInt(8),
                        resultSet.getInt(9)
                );
            }
            return card;
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("getCardById error");
        }
    }

    public void updateCardOwnerById(int uid, String cid) throws DataAccessException {
        try(PreparedStatement preparedStatement = this.uow.prepareStatement("UPDATE card SET fk_uid = ? WHERE cid = ?")) {
            preparedStatement.setInt(1, uid);
            preparedStatement.setString(2, cid);
        }
        catch (SQLException sqlException) {
            throw new DataAccessException("updateCardOwnerById");
        }
    }
}
