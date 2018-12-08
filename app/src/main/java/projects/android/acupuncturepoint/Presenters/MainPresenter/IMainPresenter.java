package projects.android.acupuncturepoint.Presenters.MainPresenter;

import java.util.List;

import projects.android.acupuncturepoint.Models.AccupuncturePointData.AccupuncturePoint;

public interface IMainPresenter {
    List<AccupuncturePoint> findLastestAcupuncturePoints(int x, int y);
}
