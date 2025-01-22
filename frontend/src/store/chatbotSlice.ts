import { createSlice, PayloadAction } from '@reduxjs/toolkit';

interface Message {
  content: string;
  isUser: boolean;
}

interface ChatbotState {
  messages: Message[];
}

const initialState: ChatbotState = {
  messages: []
};

const chatbotSlice = createSlice({
  name: 'chatbot',
  initialState,
  reducers: {
    addMessage(state, action: PayloadAction<Message>) {
      state.messages = [...state.messages, action.payload];
    },
  },
});

export const { addMessage } = chatbotSlice.actions;

export default chatbotSlice.reducer;
