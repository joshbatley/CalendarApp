import { Box } from "@edgmont-ui/react";
import { Theme } from "./Theme";
import { Title } from "./Title";

type HeaderProps = {
  changeTheme: () => void;
  theme: 'light' | 'dark';
}

export const Header: React.FC<HeaderProps> = ({ changeTheme, theme }) => (
  <Box display="flex" justifyContent="space-between" px="3" py="5" borderBottom="border.1" bg="offsetBackground">
    <Title />
    <Theme onChange={changeTheme} checked={theme === 'dark'} />
  </Box>
)