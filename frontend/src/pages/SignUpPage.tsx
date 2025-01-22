import React, { useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { login } from "../store/authSlice.ts";

const SignUpPage = () => {
  const navigate = useNavigate();

  const dispatch = useDispatch();

  const [error, setError] = useState<string>("");

  const usernameInputRef = useRef<HTMLInputElement>(null);
  const emailInputRef = useRef<HTMLInputElement>(null);
  const passwordInputRef = useRef<HTMLInputElement>(null);
  const repeatPasswordInputRef = useRef<HTMLInputElement>(null);

  const createAccount = async () => {
    setError("");
    const username = usernameInputRef.current!.value;
    const email = emailInputRef.current!.value;
    const password = passwordInputRef.current!.value;
    const repeatedPassword = repeatPasswordInputRef.current!.value;

    if (username.trim().length === 0) {
      setError("Enter valid username");
      return;
    }

    if (email.trim().length === 0) {
      setError("Enter valid email");
      return;
    }

    if (password.length < 6) {
      setError("Password has to be at least 6 characters long");
      return;
    }

    if (password !== repeatedPassword) {
      setError("Passwords should be the same");
      return;
    }

    const newUser = {
      username,
      email,
      password,
      createdAt: new Date().toISOString(),
    };

    try {
      const response = await fetch("http://localhost:8080/users", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(newUser),
      });

      const createdUserWithId = await response.json();
      dispatch(login(createdUserWithId));

      navigate("/");
    } catch (err) {
      setError("An error occurred while creating account.");
      console.error(err);
    }
  };

  return (
    <div className="mt-[150px] flex flex-col">
      <p
        className={
          "text-red-600 text-center h-[30px] " + (error === "" && "opacity-0")
        }
      >
        {error}
      </p>

      <div className="max-w-[400px] mx-auto p-4 flex flex-col gap-2 w-full">
        <input
          ref={usernameInputRef}
          placeholder="Username"
          className="input border border-black rounded-md"
          type="text"
        />
        <input
          ref={emailInputRef}
          placeholder="Email"
          className="input border border-black rounded-md"
          type="email"
        />
        <input
          ref={passwordInputRef}
          placeholder="Password"
          className="input border border-black rounded-md"
          type="password"
        />
        <input
          ref={repeatPasswordInputRef}
          placeholder="Repeat Password"
          className="input border border-black rounded-md"
          type="password"
        />
      </div>

      <button
        onClick={createAccount}
        className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border mt-[20px] mx-auto"
      >
        Create Account
      </button>
    </div>
  );
};

export default SignUpPage;
