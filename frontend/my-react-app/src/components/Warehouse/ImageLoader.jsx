import React, { useEffect, useState } from 'react';
import axios from 'axios';

const ImageLoader = ({ imageId, alt, actualToken, isEditing, handleSaveProductChanges }) => {
    const [image, setImage] = useState(null);

    const fetchImage = () => {
        axios.get(`api/warehouse/items/image/${imageId}`, {
            headers: {
                Authorization: `Bearer ${actualToken}`,
            },
            responseType: 'blob',
        })
            .then((data) => {
                const imageBlob = new Blob([data.data], { type: 'image/jpeg' });
                const imageUrl = URL.createObjectURL(imageBlob);
                setImage(imageUrl);
            })
            .catch((error) => {
                console.log('Error fetching image:', error);
            });
    };

    useEffect(() => {
        fetchImage();
    }, [imageId, actualToken]);

    const handleImageChange = (event) => {
        const imageFile = event.target.files[0];
        if (imageFile) {
            const imageUrl = URL.createObjectURL(imageFile);
            setImage(imageUrl);

            handleSaveProductChanges(imageId, 'imageFile', imageFile);
            handleSaveProductChanges(imageId, 'fileName', imageFile.name);
        }
    };

    return (
        <div>
            {image && (
                <img
                    className="product-image"
                    src={image}
                    alt={alt}
                    onClick={() => isEditing && document.getElementById(`file-input-${imageId}`).click()}
                />
            )}

            <input
                type="file"
                id={`file-input-${imageId}`}
                style={{ display: 'none' }}
                onChange={handleImageChange}
            />
        </div>
    );
};

export default ImageLoader;