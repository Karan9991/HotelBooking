package com.example.singlehotel.util;

public class Events {

    //Event used to update profile
    public static class ProfileUpdate {

        private String string;

        public ProfileUpdate(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    //Event used to update remove and update image
    public static class ProImage {

        private String string, imagePath;
        private boolean isProfile, isRemove;

        public ProImage(String string, String imagePath, boolean isProfile, boolean isRemove) {
            this.string = string;
            this.imagePath = imagePath;
            this.isProfile = isProfile;
            this.isRemove = isRemove;
        }

        public String getString() {
            return string;
        }

        public String getImagePath() {
            return imagePath;
        }

        public boolean isProfile() {
            return isProfile;
        }

        public boolean isRemove() {
            return isRemove;
        }
    }


}
