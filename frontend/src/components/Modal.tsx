import React from "react";
import { useSelector, useDispatch } from "react-redux";
import { RootState } from "../store/store";
import { closeModal } from "../store/modalSlice.ts";

const Modal = () => {
  const dispatch = useDispatch();

  const { isOpen, title, content } = useSelector(
    (state: RootState) => state.modal
  );

  if (!isOpen) return null;

  return (
    <>
      <div
        className="fixed top-0 left-0 w-full h-full bg-black bg-opacity-50 z-40"
        onClick={() => dispatch(closeModal())}
      ></div>

      <div className="fixed top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 bg-white p-4 rounded-md shadow-md z-50 w-[90%] max-w-[400px] border border-black">
        {title && <h2 className="text-xl font-bold mb-4">{title}</h2>}
        <div>{content}</div>
      </div>
    </>
  );
};

export default Modal;
