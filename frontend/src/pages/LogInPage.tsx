import React, { useRef, useState } from "react";
import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { login } from "../store/authSlice.ts";

const LogInPage = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const [error, setError] = useState<string>("");

  const emailInputRef = useRef<HTMLInputElement>(null);
  const passwordInputRef = useRef<HTMLInputElement>(null);

  const handleLogin = async () => {
    setError("");
    const email = emailInputRef.current!.value;
    const password = passwordInputRef.current!.value;

    if (email.trim().length === 0) {
      setError("Enter valid email");
      return;
    }

    if (password.length === 0) {
      setError("Enter valid password");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/users?email=${email}`
      );

      const data = await response.json();
      if (Object.keys(data).includes("error")) {
        setError("Email not found.");
        return;
      }
      if (data.password !== password) {
        setError("Incorrect password.");
        return;
      }

      dispatch(login(data));

      navigate("/");
    } catch (err) {
      setError("An error occurred while logging in.");
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
      </div>

      <button
        onClick={handleLogin}
        className="text-white border-black bg-[#0309c5] px-6 rounded-md duration-300 hover:opacity-80 py-3 border mt-[20px] mx-auto"
      >
        Log In
      </button>
    </div>
  );
};

export default LogInPage;
