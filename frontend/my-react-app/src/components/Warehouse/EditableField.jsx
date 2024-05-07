import React, {useState} from 'react';

const EditableField = ({value, onChange = false}) => {
    const [isEditing, setIsEditing] = useState(false);
    const [editedValue, setEditedValue] = useState(value);

    const handleDoubleClick = () => {
        setIsEditing(true);
    };

    const handleChange = (event) => {
        setEditedValue(event.target.value);
    };

    const handleKeyPress = (event) => {
        if (event.key === 'Enter') {
            setIsEditing(false);
            onChange(editedValue);
        }
    };

    const handleBlur = () => {
        setIsEditing(false);
        onChange(editedValue); // Вызываем onSave при потере фокуса, если нужно сохранить изменения
    };

    return (
        <div className="product-info2">
            {isEditing ? (
                <input
                    type="text"
                    value={editedValue}
                    onChange={handleChange}
                    onKeyPress={handleKeyPress}
                    onBlur={handleBlur}
                    autoFocus
                />
            ) : (
                <span onDoubleClick={handleDoubleClick}>{value}</span>
            )}
        </div>
    );
};

export default EditableField;
