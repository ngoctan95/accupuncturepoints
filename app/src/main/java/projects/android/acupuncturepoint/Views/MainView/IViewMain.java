package projects.android.acupuncturepoint.Views.MainView;

import projects.android.acupuncturepoint.Models.AccupuncturePointData.AccupuncturePoint;

public interface IViewMain {
    void showInfoView(AccupuncturePoint accupuncturePoint);

    void dismissInfoView();

    void showHintAnotherImgAc();
}
