import React, { useEffect, useRef, useState } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../store/store";
import { useDispatch } from "react-redux";
import { addMessage } from "../store/chatbotSlice.ts";

const Chatbot = () => {
  const dispatch = useDispatch();
  const [message, setMessage] = useState<string>("");
  const messageInputRef = useRef<HTMLInputElement>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const messages = useSelector((state: RootState) => state.chatbot.messages);
  const posts = useSelector((state: RootState) => state.posts.posts);

  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [messages]);

  const handleSendMessage = async (message: string) => {
    dispatch(addMessage({ isUser: true, content: message }));

    const response = await fetch("http://localhost:8080/chatbot", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        message,
        context: JSON.stringify(posts),
        history: JSON.stringify(messages),
      }),
    });

    if (!response.ok) {
      throw new Error("Failed to get a response from the chatbot");
    }

    const chatbotResponse = await response.json();

    if (chatbotResponse?.output) {
      dispatch(addMessage({ isUser: false, content: chatbotResponse.output }));
    } else {
      dispatch(
        addMessage({
          isUser: false,
          content: "Sorry man, now I do not want to talk.",
        })
      );
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent) => {
    if (e.key === "Enter" && message.trim() !== "") {
      handleSendMessage(message);
      setMessage("");
    }
  };

  return (
    <div className="fixed h-[400px] bottom-[100px] z-10 w-full flex animate-show">
      <div className="flex-col bg-white border border-black rounded-md flex mx-auto max-w-[550px] w-full animate-show">
        <h2 className="font-bold border-b border-black rounded-md py-2 px-3 animate-show">
          Chatbot
        </h2>

        <div className="m-2 rounded-md mt-2 h-full overflow-y-auto">
          {messages.length === 0 ? (
            <p className="mx-auto text-gray-500 animate-show">
              Start conversation to see messages.
            </p>
          ) : (
            <ul className="p-2 animate-show">
              {messages.map((message, index) => (
                <li className="flex flex-col">
                  <p
                    className={"font-bold " + (message.isUser ? "ml-auto" : "")}
                  >
                    {message.isUser ? "You" : "AI"}
                  </p>
                  <div
                    key={index}
                    className={
                      "mb-2 rounded-md animate-show " +
                      (message.isUser &&
                        "border border-black bg-[#0309c5] text-white px-2 py-1 ml-auto w-fit")
                    }
                  >
                    <p>{message.content}</p>
                  </div>
                </li>
              ))}
            </ul>
          )}
          {/* Invisible div to scroll to */}
          <div ref={messagesEndRef} />
        </div>

        <div className="p-2">
          <input
            ref={messageInputRef}
            placeholder="Send message"
            className="input border border-black rounded-md animate-show"
            type="text"
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            onKeyDown={handleKeyDown}
          />
        </div>
      </div>
    </div>
  );
};

export default Chatbot;
