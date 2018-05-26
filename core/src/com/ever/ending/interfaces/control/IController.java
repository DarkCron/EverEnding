package com.ever.ending.interfaces.control;

public interface IController {
    enum GameActions{
        MOVE_UP, MOVE_DOWN, MOVE_LEFT, MOVE_RIGHT,
        MOVE_CAMERA_UP, MOVE_CAMERA_DOWN, MOVE_CAMERA_LEFT, MOVE_CAMERA_RIGHT, ZOOM_CAMERA_IN, ZOOM_CAMERA_OUT,
        CONFIRM, CANCEL,ESC,MENU,TAB
    }

    enum KnownMouseButtons{
        LEFT(0),RIGHT(1),MIDDLE(2);

        public int getButton() {
            return button;
        }

        private final int button;

        private KnownMouseButtons(int button){
            this.button = button;
        }
    }
}
