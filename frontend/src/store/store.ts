import { configureStore } from "@reduxjs/toolkit";
import authReducer from "./authSlice.ts";
import postsReducer from "./postsSlice.ts";
import modalReducer from "./modalSlice.ts";
import chatbotReducer from "./chatbotSlice.ts";

const store = configureStore({
  reducer: {
    auth: authReducer,
    posts: postsReducer,
    modal: modalReducer,
    chatbot: chatbotReducer,
  },
});

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;

export default store;
