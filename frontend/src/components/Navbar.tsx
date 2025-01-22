import React, { useState } from "react";
import { useSelector } from "react-redux";
import { Link, useNavigate } from "react-router-dom";
import { RootState } from "../store/store";
import { useDispatch } from "react-redux";
import { closeModal, openModal } from "../store/modalSlice.ts";
import { logout } from "../store/authSlice.ts";
import axios from "axios";
import { setPosts, setUpvotes } from "../store/postsSlice.ts";

export const Navbar = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [accountDropdownVisibility, setAccountDropdownVisibiliy] =
    useState(false);

  const isUserLoggedIn = useSelector(
    (state: RootState) => state.auth.isUserLoggedIn
  );
  const user = useSelector((state: RootState) => state.auth.user);

  const handleLogOut = () => {
    const logUserOut = () => {
      dispatch(logout());
      dispatch(closeModal());
      navigate("/");
    };

    dispatch(
      openModal({
        title: "Are you sure to log out?",
        content: (
          <div className="flex justify-between mt-[20px]">
            <button
              onClick={() => dispatch(closeModal())}
              className="border-black px-6 rounded-md duration-300 hover:opacity-80 py-3 border animate-show"
            >
              Cancel
            </button>
            <button
              onClick={logUserOut}
              className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border animate-show"
            >
              Log Out
            </button>
          </div>
        ),
      })
    );
  };

  const handleDeleteAccount = () => {
    const userId = user?.id;

    const deleteAccount = async () => {
      await fetch(`http://localhost:8080/users/${userId}`, {
        method: "DELETE",
      });
      dispatch(closeModal());
      dispatch(logout());

      let response = await axios.get("http://localhost:8080/posts");
      dispatch(setPosts(response.data));

      response = await axios.get("http://localhost:8080/upvotes");
      dispatch(setUpvotes(response.data));

      navigate("/");
    };

    dispatch(
      openModal({
        title: "Are you sure to delete your account?",
        content: (
          <div className="flex justify-between mt-[20px]">
            <button
              onClick={() => dispatch(closeModal())}
              className="border-black px-6 rounded-md duration-300 hover:opacity-80 py-3 border animate-show"
            >
              Cancel
            </button>
            <button
              onClick={deleteAccount}
              className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border animate-show"
            >
              Delete
            </button>
          </div>
        ),
      })
    );
  };

  return (
    <div className="fixed top-0 w-full border-b border-black bg-[#ffffffe7] z-10">
      <div className="flex justify-between items-center">
        <Link to="/">
          <p className="logo-title px-6 animate-show">Senti</p>
        </Link>

        <div
          onMouseEnter={() => setAccountDropdownVisibiliy(true)}
          onMouseLeave={() => setAccountDropdownVisibiliy(false)}
          className="relative py-4 px-8 "
        >
          <p className="cursor-pointer animate-show">Account</p>

          {accountDropdownVisibility && (
            <div className="absolute -left-[80px] top-[56px] border-black border rounded-b-md p-4 w-[180px] flex flex-col gap-2 bg-[#ffffffe7] animate-show">
              {!isUserLoggedIn ? (
                <>
                  <Link
                    to="/login"
                    className="hover:text-[#0309c5] duration-300 animate-show"
                  >
                    Log In
                  </Link>
                  <Link
                    to="/signup"
                    className="hover:text-[#0309c5] duration-300 animate-show"
                  >
                    Sign Up
                  </Link>
                </>
              ) : (
                <>
                  <button
                    onClick={handleLogOut}
                    className="hover:text-[#0309c5] duration-300 text-left animate-show"
                  >
                    Log Out
                  </button>
                  <button
                    onClick={handleDeleteAccount}
                    className="hover:text-[#0309c5] duration-300 text-left animate-show"
                  >
                    Delete Account
                  </button>
                </>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};
