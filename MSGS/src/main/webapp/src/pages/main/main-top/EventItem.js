import React from "react";

import styles from "./MainTop.module.css";

const EventItem = (props) => {
    return (
        <a className={styles["event-item"]} href="">
            <div>
                <p>{props.text1}</p>
                <p>{props.text2}</p>
            </div>
            <div className={styles["event-img-div"]}>
                <img />
            </div>
        </a>
    );
};

export default EventItem;