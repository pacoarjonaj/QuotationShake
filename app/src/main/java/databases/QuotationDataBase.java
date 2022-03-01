package databases;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import model.Quotation;

@Database(entities = {Quotation.class}, version = 1)
public abstract class QuotationDataBase extends RoomDatabase {

    private static QuotationDataBase quotationDataBase;

    public synchronized static QuotationDataBase getInstance(Context context){
        if(quotationDataBase == null){
            quotationDataBase = Room.databaseBuilder(context,QuotationDataBase.class, "quotation_database")
                                    .build();
        }
        return quotationDataBase;
    }

    public abstract QuotationDAO quotationDAO();
}
