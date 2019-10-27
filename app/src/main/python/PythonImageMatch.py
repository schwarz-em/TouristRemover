#Copied Code that stiches 2 images together
import numpy as np
import cv2


def get_image(filename):
    return cv2.imread(filename)

def gen_hom_matrix(base, curr):
    # load images

    #cv2.imshow('base', base)

    # convert to grayscale
    base_gray = cv2.cvtColor(base, cv2.COLOR_BGR2GRAY)

    # find the coordinates of good features to track  in base
    base_features = cv2.goodFeaturesToTrack(base_gray, 3000, .01, 10)

    # find corresponding features in current photo
    curr_features = np.array([])
    curr_features, pyr_stati, _ = cv2.calcOpticalFlowPyrLK(base, curr, base_features, curr_features, flags=1)

    # only add features for which a match was found to the pruned arrays
    base_features_pruned = []
    curr_features_pruned = []
    for index, status in enumerate(pyr_stati):
        if status == 1:
            base_features_pruned.append(base_features[index])
            curr_features_pruned.append(curr_features[index])

    # convert lists to numpy arrays so they can be passed to opencv function
    bf_final = np.asarray(base_features_pruned)
    cf_final = np.asarray(curr_features_pruned)

    # find perspective transformation using the arrays of corresponding points
    transformation, hom_stati = cv2.findHomography(cf_final, bf_final, method=cv2.RANSAC, ransacReprojThreshold=1)
    return transformation, hom_stati
    # transform the images and overlay them to see if they align properly
    # not what I do in the actual program, just for use in the example code
    # so that you can see how they align, if you decide to run it
def gen_image(curr, base, transformation):
    height, width = curr.shape[:2]
    mod_photo = cv2.warpPerspective(curr, transformation, (width, height))
    new_image = cv2.addWeighted(mod_photo, .5, base, .5, 1)
    return new_image

def stich_multiple_image(image_list):
    base = image_list[0]
    height, width = base.shape[:2]
    print(height, width)
    mod_photo = []
    for element in range(1, len(image_list)):
        matrix, _ = gen_hom_matrix(base, image_list[element])
        mod_photo.append(cv2.warpPerspective(image_list[element], matrix, (width, height)))
    base = cv2.addWeighted(base, 1/(2*len(image_list)), base, 1/(2*len(image_list)), 1)
    for photo in mod_photo:
        base = cv2.addWeighted(photo, 1/len(image_list), base, 1, 1)
    return base

if __name__ == "__main__":
    images = ['image4.jpg', 'image2.jpg', 'image3.jpg', 'image1.jpg']
    cv2.imwrite('new.jpg',stich_multiple_image([get_image(file) for file in images]))
