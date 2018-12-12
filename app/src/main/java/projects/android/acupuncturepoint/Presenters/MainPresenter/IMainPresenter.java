package projects.android.acupuncturepoint.Presenters.MainPresenter;

import java.util.List;

import projects.android.acupuncturepoint.Models.AccupuncturePointData.AccupuncturePoint;

public interface IMainPresenter {
    List<AccupuncturePoint> getListAcupuncture(int currentImg);

    int findIdWithLastestPoint(float x, float y, int currentImg);

    void findAcupuncturePoint(float x, float y, int currentImg, int width, int height);
}
